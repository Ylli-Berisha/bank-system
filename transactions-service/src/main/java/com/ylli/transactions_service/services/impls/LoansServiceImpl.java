package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
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
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LoansServiceImpl extends BaseServiceImpl<Loan, LoanDto, Long, LoansRepository, LoansMapper> implements LoansService {

    private final AccountsFeignClient accountsFeignClient;
    private static final Logger log = LoggerFactory.getLogger(LoansServiceImpl.class);
    private final UsersFeignClient usersFeignClient;

    public LoansServiceImpl(LoansRepository repository, LoansMapper mapper, AccountsFeignClient accountsFeignClient, UsersFeignClient usersFeignClient) {
        super(repository, mapper);
        this.accountsFeignClient = accountsFeignClient;
        this.usersFeignClient = usersFeignClient;
    }

    @Override
    public List<LoanDto> getUserLoans(String userId, LoanStatus status) {
        List<AccountDto> accounts = accountsFeignClient.getUserAccounts(userId).getBody();

        if (accounts == null || accounts.isEmpty()) {
            return List.of();
        }

        List<Loan> allUserLoans = new ArrayList<>();

        for (AccountDto accountDto : accounts) {
            Account tempAccount = new Account();
            tempAccount.setId(accountDto.getId());

            if (status != null) {
                allUserLoans.addAll(repository.findByAccountAndStatus(tempAccount, status));
            } else {
                allUserLoans.addAll(repository.findByAccount(tempAccount));
            }
        }

        if (allUserLoans.isEmpty()) {
            return Collections.emptyList();
        } else {
            return mapper.toDtoList(allUserLoans);
        }
    }

    @Override
    public List<String> getLoanTypes() {
        List<LoanType> loanTypes = List.of(LoanType.values());
        return loanTypes.stream()
                .map(LoanType::name)
                .toList();
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
            LocalDate today = LocalDate.now();
//        loan.setEndDate(today.plusMonths(loanApplicationRequestDto.getTermInMonths()));
            loan.setStatus(LoanStatus.PENDING);
//        loan.setStartDate(today);
            loan.setTermInMonths(loanApplicationRequestDto.getTermInMonths());

            repository.save(loan);
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
    public List<LoanDto> filterUserLoans(
            String userId,
            String loanTypeString,
            String statusString,
            String startDateString,
            String endDateString,
            Double minAmount,
            Double maxAmount,
            String query
    ) {
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
                System.err.println("Warning: Received invalid LoanStatus string: " + statusString);
                throw e;
            }
        }

        LoanType parsedLoanType = null;
        if (loanTypeString != null && !loanTypeString.isEmpty()) {
            try {
                parsedLoanType = LoanType.valueOf(loanTypeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid LoanType string: " + loanTypeString);
                throw e;
            }
        }

        Double actualMinAmount = (minAmount != null) ? minAmount : 0.0;
        Double actualMaxAmount = (maxAmount != null) ? maxAmount : Double.MAX_VALUE;

        List<Loan> loans = repository.findAll(
                LoanSpecifications.withFilters(
                        userId,
                        parsedLoanType,
                        parsedStatus,
                        parsedStartDate,
                        parsedEndDate,
                        actualMinAmount,
                        actualMaxAmount,
                        query
                )
        );

        return mapper.toDtoList(loans);
    }

    @Override
    public Page<LoanDto> filterAdminLoans(
            String adminId,
            String userId,
            String username,
            String email,
            String typeString,
            String statusString,
            String startDate,
            String endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page,
            int size
    ) {
        validateAdmin(adminId);

        LoanStatus parsedStatus = null;
        if (statusString != null && !statusString.isEmpty()) {
            try {
                parsedStatus = LoanStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Invalid LoanStatus: " + statusString);
                throw e;
            }
        }

        LoanType parsedType = null;
        if (typeString != null && !typeString.isEmpty()) {
            try {
                parsedType = LoanType.valueOf(typeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Invalid LoanType: " + typeString);
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

        Page<Loan> loansPage = repository.findAll(
                AdminLoanSpecifications.withFilters(
                        userId,
                        username,
                        email,
                        parsedType,
                        parsedStatus,
                        parsedStartDate,
                        parsedEndDate,
                        actualMinAmount,
                        actualMaxAmount
                ),
                pageable
        );

        return loansPage.map(mapper::toDto);
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

}
