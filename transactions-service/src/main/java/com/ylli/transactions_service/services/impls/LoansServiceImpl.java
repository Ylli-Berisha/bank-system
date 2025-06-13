package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.enums.LoanType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import com.ylli.transactions_service.configs.LoanSpecifications;
import com.ylli.transactions_service.mappers.LoansMapper;
import com.ylli.transactions_service.repositories.LoansRepository;
import com.ylli.transactions_service.services.LoansService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LoansServiceImpl extends BaseServiceImpl<Loan, LoanDto, Long, LoansRepository, LoansMapper> implements LoansService {

    private final AccountsFeignClient accountsFeignClient;
    private static final Logger log = LoggerFactory.getLogger(LoansServiceImpl.class);

    public LoansServiceImpl(LoansRepository repository, LoansMapper mapper, AccountsFeignClient accountsFeignClient) {
        super(repository, mapper);
        this.accountsFeignClient = accountsFeignClient;
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
                throw new EntityNotFoundException("Account with ID " + accountId + " not found.");
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
        }catch (EntityNotFoundException e) {
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
            }
        }

        LoanType parsedLoanType = null;
        if (loanTypeString != null && !loanTypeString.isEmpty()) {
            try {
                parsedLoanType = LoanType.valueOf(loanTypeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid LoanType string: " + loanTypeString);
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
}
