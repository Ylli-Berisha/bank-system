package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.AuditHelper;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.AuditType;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.enums.LoanType;
import com.ylli.shared.enums.UserRole;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.transactions_service.configs.AdminLoanSpecifications;
import com.ylli.transactions_service.configs.LoanSpecifications;
import com.ylli.transactions_service.mappers.LoansMapper;
import com.ylli.transactions_service.repositories.LoansRepository;
import com.ylli.transactions_service.services.LoansService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LoansServiceImpl extends BaseServiceImpl<Loan, LoanDto, Long, LoansRepository, LoansMapper> implements LoansService {

    private final AccountsFeignClient accountsFeignClient;
    private static final Logger log = LoggerFactory.getLogger(LoansServiceImpl.class);
    private final UsersFeignClient usersFeignClient;
    private final LoanProcessingService loanProcessingService;
    private final AuditHelper auditHelper;
    private final CacheManager cacheManager;

    public LoansServiceImpl(LoansRepository repository, LoansMapper mapper, AccountsFeignClient accountsFeignClient,
                            UsersFeignClient usersFeignClient, LoanProcessingService loanProcessingService,
                            AuditHelper auditHelper, CacheManager cacheManager) {
        super(repository, mapper);
        this.accountsFeignClient = accountsFeignClient;
        this.usersFeignClient = usersFeignClient;
        this.loanProcessingService = loanProcessingService;
        this.auditHelper = auditHelper;
        this.cacheManager = cacheManager;
    }

    @Cacheable(
            value = "userLoans",
            key = "#userId + '-' + (#status != null ? #status.name() : 'ALL') + '-' + #page + '-' + #size"
    )
    @Override
    public Page<LoanDto> getUserLoans(String userId, LoanStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<AccountDto> accounts = accountsFeignClient.getUserAccounts(userId).getBody();

        if (accounts == null || accounts.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Account> accountEntities = accounts.stream()
                .map(dto -> {
                    Account a = new Account();
                    a.setId(dto.getId());
                    return a;
                })
                .collect(Collectors.toList());

        Page<Loan> loanPage;

        if (status != null) {
            loanPage = repository.findByAccountInAndStatus(accountEntities, status, pageable);
        } else {
            loanPage = repository.findByAccountIn(accountEntities, pageable);
        }

        List<LoanDto> loanDtos = mapper.toDtoList(loanPage.getContent());

        return new PageImpl<>(loanDtos, pageable, loanPage.getTotalElements());
    }


    @Override
    public List<String> getLoanTypes() {
        List<LoanType> loanTypes = List.of(LoanType.values());
        return loanTypes.stream().map(LoanType::name).toList();
    }

    @Override
    @Transactional
    public Boolean applyForLoan(String accountId, LoanApplicationRequestDto loanApplicationRequestDto, String userId) {
        try {
            AccountDto accountDto = accountsFeignClient.getById(accountId).getBody();
            if (accountDto == null) {
                throw new ResourceNotFoundException("Account with ID " + accountId + " not found.");
            }
            if (!accountDto.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Account with ID " + accountId + " does not belong to user with ID " + userId);
            }

            Account account = new Account();
            account.setId(accountDto.getId());

            Loan loan = new Loan();
            loan.setAccount(account);
            loan.setAmount(loanApplicationRequestDto.getAmount());
            loan.setLoanType(loanApplicationRequestDto.getLoanType());
            loan.setInterestRate(loanApplicationRequestDto.getInterestRate());

            BigDecimal amount = loanApplicationRequestDto.getAmount();

            BigDecimal interestRate = BigDecimal.valueOf(loanApplicationRequestDto.getInterestRate()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            int termInMonths = loanApplicationRequestDto.getTermInMonths();

            BigDecimal totalWithInterest = amount.multiply(BigDecimal.ONE.add(interestRate));
            BigDecimal monthlyInstallment = totalWithInterest.divide(BigDecimal.valueOf(termInMonths), 2, RoundingMode.HALF_UP);
            loan.setMonthlyInstallment(monthlyInstallment);
            loan.setAmount(totalWithInterest);
            loan.setLeftAmount(amount);
            loan.setStatus(LoanStatus.PENDING);
            loan.setTermInMonths(termInMonths);

            auditHelper.createAudit(
                    AuditType.LOAN_APPLIED,
                    "User with ID " + userId + " applied for a loan with ID " + loan.getId() + " for account ID " + accountId,
                    accountId
            );

            repository.save(loan);
            evictUserLoansCache(userId);
            return true;
        } catch (EntityNotFoundException e) {
            log.warn("Loan application failed: Account with ID {} not found for user {}. Error: {}", accountId, userId, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("Loan application failed: Account ID {} does not belong to user {}. Error: {}", accountId, userId, e.getMessage());
            throw e;
        } catch (FeignException e) {
            log.error("Loan application failed due to communication error with accounts service for account ID {}. Status: {}, Error: {}", accountId, e.status(), e.getMessage(), e);
            throw new RuntimeException("Failed to communicate with account service.", e);
        } catch (DataAccessException e) {
            log.error("Loan application failed due to a database error for account ID {}. Error: {}", accountId, e.getMessage(), e);
            throw new RuntimeException("Failed to save loan application due to a database error.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred during loan application for account ID {}. Error: {}", accountId, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during loan application.", e);
        }
    }

    @Override
    public List<LoanDto> filterUserLoans(String userId, String loanTypeString, String statusString, String startDateString, String endDateString, Double minAmount, Double maxAmount, String query) {
        LocalDate parsedStartDate = null;
        if (startDateString != null && !startDateString.isEmpty()) {
            parsedStartDate = LocalDate.parse(startDateString);
        }

        LocalDate parsedEndDate = null;
        if (endDateString != null && !endDateString.isEmpty()) {
            parsedEndDate = LocalDate.parse(endDateString);
        }

        LoanStatus parsedStatus = null;
        if (statusString != null && !statusString.isEmpty()) {
            try {
                parsedStatus = LoanStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Received invalid LoanStatus string: {}", statusString);
                throw e;
            }
        }

        LoanType parsedLoanType = null;
        if (loanTypeString != null && !loanTypeString.isEmpty()) {
            try {
                parsedLoanType = LoanType.valueOf(loanTypeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Received invalid LoanType string: {}", loanTypeString);
                throw e;
            }
        }

        Double actualMinAmount = (minAmount != null) ? minAmount : 0.0;
        Double actualMaxAmount = (maxAmount != null) ? maxAmount : Double.MAX_VALUE;

        List<Loan> loans = repository.findAll(LoanSpecifications.withFilters(userId, parsedLoanType, parsedStatus, parsedStartDate, parsedEndDate, actualMinAmount, actualMaxAmount, query));

        return mapper.toDtoList(loans);
    }

    @Override
    public Page<LoanDto> filterAdminLoans(String adminId, String userId, String username, String email, String typeString, String statusString, String startDate, String endDate, BigDecimal minAmount, BigDecimal maxAmount, int page, int size) {
        validateAdmin(adminId);

        LoanStatus parsedStatus = null;
        if (statusString != null && !statusString.isEmpty()) {
            try {
                parsedStatus = LoanStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid LoanStatus: {}", statusString);
                throw e;
            }
        }

        LoanType parsedType = null;
        if (typeString != null && !typeString.isEmpty()) {
            try {
                parsedType = LoanType.valueOf(typeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid LoanType: {}", typeString);
                throw e;
            }
        }

        LocalDate parsedStartDate = null;
        if (startDate != null && !startDate.isEmpty()) {
            parsedStartDate = LocalDate.parse(startDate);
        }

        LocalDate parsedEndDate = null;
        if (endDate != null && !endDate.isEmpty()) {
            parsedEndDate = LocalDate.parse(endDate);
        }

        BigDecimal actualMinAmount = (minAmount != null) ? minAmount : BigDecimal.valueOf(0.0);
        BigDecimal actualMaxAmount = (maxAmount != null) ? maxAmount : BigDecimal.valueOf(999999999999999.99);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Loan> loansPage = repository.findAll(AdminLoanSpecifications.withFilters(userId, username, email, parsedType, parsedStatus, parsedStartDate, parsedEndDate, actualMinAmount, actualMaxAmount), pageable);

        return loansPage.map(mapper::toDto);
    }

    @Override
    @Transactional
    public LoanDto acceptLoan(Long loanId, String adminId) {
        validateAdmin(adminId);
        Loan loan = repository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID " + loanId));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Only pending loans can be accepted.");
        }

        auditHelper.createAudit(
                AuditType.LOAN_APPROVED,
                "Loan with ID " + loanId + " has been approved by admin with ID " + adminId,
                loan.getAccount().getId()
        );

        LoanDto acceptedLoan = startLoan(loan);
        if (loan.getAccount() != null && loan.getAccount().getUser() != null) {
            evictUserLoansCache(loan.getAccount().getUser().getId());
        } else {
            log.warn("Could not evict cache for accepted loan {}: User ID not found.", loanId);
        }
        return acceptedLoan;
    }

    @Transactional
    public LoanDto rejectLoan(Long loanId, String adminId) {
        validateAdmin(adminId);
        Loan loan = repository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID " + loanId));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Only pending loans can be rejected.");
        }

        loan.setStatus(LoanStatus.REJECTED);

        auditHelper.createAudit(
                AuditType.LOAN_REJECTED,
                "Loan with ID " + loanId + " has been rejected by admin with ID " + adminId,
                loan.getAccount().getId()
        );

        repository.save(loan);
        if (loan.getAccount() != null && loan.getAccount().getUser() != null) {
            evictUserLoansCache(loan.getAccount().getUser().getId());
        } else {
            log.warn("Could not evict cache for rejected loan {}: User ID not found.", loanId);
        }
        return mapper.toDto(loan);
    }

    @Override
    @Transactional
    public LoanDto acceptProposedChanges(Long loanId, String userId) {
        Loan loan = repository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID " + loanId));

        if (!userId.equals(loan.getAccount().getUser().getId())) {
            throw new IllegalArgumentException("User with ID " + userId + " does not own the loan with ID " + loanId);
        }

        if (loan.getStatus() != LoanStatus.CHANGES_PROPOSED) {
            throw new IllegalStateException("Only loans with proposed changes can be accepted.");
        }

        auditHelper.createAudit(
                AuditType.LOAN_CHANGES_ACCEPTED,
                "Loan with ID " + loanId + " has been accepted by user with ID " + userId,
                loan.getAccount().getId()
        );

        LoanDto acceptedLoan = startLoan(loan);
        evictUserLoansCache(userId);
        return acceptedLoan;
    }

    @Override
    @Transactional
    public LoanDto rejectProposedChanges(Long loanId, String userId) {
        Loan loan = repository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID " + loanId));

        if (!userId.equals(loan.getAccount().getUser().getId())) {
            throw new IllegalArgumentException("User with ID " + userId + " does not own the loan with ID " + loanId);
        }

        if (loan.getStatus() != LoanStatus.CHANGES_PROPOSED) {
            throw new IllegalStateException("Only loans with proposed changes can be rejected.");
        }

        loan.setStatus(LoanStatus.REJECTED);

        auditHelper.createAudit(
                AuditType.LOAN_CHANGES_REJECTED,
                "Loan with ID " + loanId + " has been rejected by user with ID " + userId,
                loan.getAccount().getId()
        );

        repository.save(loan);
        evictUserLoansCache(userId);
        return mapper.toDto(loan);
    }

    @Override
    public List<LoanDto> getTopActiveLoans(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID is required to get top active loans.");
        }
        var user = usersFeignClient.getUser(userId).getBody();
        if (user == null) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found.");
        }
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "startDate"));
        List<Loan> loans;
        try{
            loans  = repository.findTop4ActiveLoansByUserId(userId, pageable);
        }
        catch (Exception e){
            log.error("Error fetching top active loans for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch top active loans for user " + userId, e);
        }
        if (loans == null || loans.isEmpty()) {
            return List.of();
        }
        return mapper.toDtoList(loans);
    }

    private void validateAdmin(String adminId) {
        if (adminId == null || adminId.isBlank()) {
            throw new IllegalArgumentException("Admin ID is required.");
        }
        var adminUser = usersFeignClient.getUser(adminId).getBody();
        if (adminUser == null || !adminUser.getRoles().contains(UserRole.ROLE_ADMIN)) {
            throw new IllegalArgumentException("User with ID " + adminId + " is not an admin.");
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void processLoanInstallments() {
        log.info("Scheduled loan installment processing started");

        List<Loan> loans = repository.findByStatus(LoanStatus.ACTIVE);

        Set<String> affectedUserIds = loans.stream()
                .filter(loan -> loan.getAccount() != null && loan.getAccount().getUser() != null)
                .map(loan -> loan.getAccount().getUser().getId())
                .collect(Collectors.toSet());

        for (String userId : affectedUserIds) {
            evictUserLoansCache(userId);
        }

        log.info("Scheduled loan installment processing finished");
    }

    private LoanDto startLoan(Loan loan) {
        LocalDate now = LocalDate.now();
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setStartDate(now);
        loan.setEndDate(loan.getStartDate().plusMonths(loan.getTermInMonths()));
        loan.setNextInstallmentDate(now.plusMonths(1));
        loan.setLeftAmount(loan.getAmount());

        repository.save(loan);
        return mapper.toDto(loan);
    }

    private void evictUserLoansCache(String userId) {
        Cache userLoansCache = cacheManager.getCache("userLoans");
        if (userLoansCache == null) {
            log.warn("Cache 'userLoans' not found, skipping eviction for user {}", userId);
            return;
        }

        List<Integer> commonPageSizes = List.of(6, 10, 20, 50, 100);
        List<LoanStatus> allLoanStatuses = List.of(LoanStatus.values());

        for (int pageSize : commonPageSizes) {
            for (int page = 0; page < 5; page++) {
                String allKey = userId + "-" + "ALL" + "-" + page + "-" + pageSize;
                userLoansCache.evict(allKey);
                for (LoanStatus status : allLoanStatuses) {
                    String statusKey = userId + "-" + status.name() + "-" + page + "-" + pageSize;
                    userLoansCache.evict(statusKey);
                }
            }
        }
        log.info("Evicted userLoans cache for user ID: {}", userId);
    }
}