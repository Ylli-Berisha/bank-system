package com.ylli.transactions_service.services.impls;

import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.models.Loan;
import com.ylli.transactions_service.repositories.LoansRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class LoanProcessingService  {

    private final LoansRepository repository;
    private final AccountsFeignClient accountsFeignClient;
    private static final Logger log = LoggerFactory.getLogger(LoanProcessingService.class);

    public LoanProcessingService(LoansRepository repository, AccountsFeignClient accountsFeignClient) {
        this.repository = repository;
        this.accountsFeignClient = accountsFeignClient;
    }


    @Transactional
    public void processSingleLoanInstallment(Loan loan) {
        LocalDate today = LocalDate.now();

        if (loan.getNextInstallmentDate() == null || loan.getNextInstallmentDate().isAfter(today)) {
            return;
        }

        if (loan.getLeftAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Loan {} has negative left amount - skipping installment", loan.getId());
            return;
        }

        if (loan.getLeftAmount().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.REPAID);
            repository.save(loan);
            log.info("Loan {} is already fully repaid", loan.getId());
            return;
        }

        AccountDto account = accountsFeignClient.getById(loan.getAccount().getId()).getBody();
        if (account == null || account.getId() == null) {
            log.warn("Account with ID {} not found for loan {} - skipping installment", loan.getAccount().getId(), loan.getId());
            return;
        }

        BigDecimal installmentAmount;
        if (loan.getNextInstallmentDate().isEqual(today)) {
            installmentAmount = loan.getMonthlyInstallment();
        } else {
            double dailyInterestRate = loan.getInterestRate() / 360.0;
            int daysOverdue = (int) java.time.temporal.ChronoUnit.DAYS.between(loan.getNextInstallmentDate(), today);
            BigDecimal overdueInterest = loan.getLeftAmount()
                    .multiply(BigDecimal.valueOf(dailyInterestRate / 100))
                    .multiply(BigDecimal.valueOf(daysOverdue))
                    .setScale(2, RoundingMode.HALF_UP);
            installmentAmount = loan.getMonthlyInstallment().add(overdueInterest);
        }

        if (account.getBalance().compareTo(installmentAmount) < 0) {
            log.warn("Insufficient funds for loan {} - skipping installment", loan.getId());
            return;
        }

        account.setBalance(account.getBalance().subtract(installmentAmount));
        accountsFeignClient.updateAccount(account.getId(), account);

        loan.setLeftAmount(loan.getLeftAmount().subtract(installmentAmount));

        if (loan.getLeftAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setLeftAmount(BigDecimal.ZERO);
            loan.setStatus(LoanStatus.REPAID);
        } else {
            loan.setNextInstallmentDate(loan.getNextInstallmentDate().plusMonths(1));
        }

        repository.save(loan);
    }


}
