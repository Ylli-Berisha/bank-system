package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.AuditHelper;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.AuditFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.enums.AuditType;
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

// ... all your imports remain the same

@Service
public class LoanProcessingService  {

    private final LoansRepository repository;
    private final AccountsFeignClient accountsFeignClient;
    private static final Logger log = LoggerFactory.getLogger(LoanProcessingService.class);
    private final AuditHelper auditHelper;

    public LoanProcessingService(LoansRepository repository, AccountsFeignClient accountsFeignClient, AuditHelper auditHelper) {
        this.repository = repository;
        this.accountsFeignClient = accountsFeignClient;
        this.auditHelper = auditHelper;
    }

    @Transactional
    public void processSingleLoanInstallment(Loan loan) {
        LocalDate today = LocalDate.now();

        if (loan.getNextInstallmentDate() == null || loan.getNextInstallmentDate().isAfter(today)) {
            return;
        }

        if (loan.getLeftAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Loan {} has negative left amount - returning the installment amount and setting to paid", loan.getId());
            loan.getAccount().setBalance(
                    loan.getAccount().getBalance().add(loan.getLeftAmount())
            );
            loan.setLeftAmount(BigDecimal.ZERO);
            loan.setStatus(LoanStatus.REPAID);

            auditHelper.createAudit(AuditType.LOAN_REPAID, "Loan with ID " + loan.getId() + " has been fully repaid due to negative left amount.", loan.getAccount().getId());
            return;
        }

        if (loan.getLeftAmount().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.REPAID);
            repository.save(loan);
            log.info("Loan {} is already fully repaid", loan.getId());

            auditHelper.createAudit(AuditType.LOAN_REPAID, "Loan with ID " + loan.getId() + " is already fully repaid.", loan.getAccount().getId());
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
            auditHelper.createAudit(AuditType.NOT_ENOUGH_FUNDS,
                    "Insufficient funds for loan with ID " + loan.getId() +
                            ". Account balance: " + account.getBalance() +
                            ", Required installment: " + installmentAmount,
                    account.getId());
            return;
        }

        account.setBalance(account.getBalance().subtract(installmentAmount));
        var response = accountsFeignClient.updateAccount(account.getId(), account);
        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to update account balance for account ID " + account.getId());
        }

        loan.setLeftAmount(loan.getLeftAmount().subtract(installmentAmount));

        if (loan.getLeftAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setLeftAmount(BigDecimal.ZERO);
            loan.setStatus(LoanStatus.REPAID);

            auditHelper.createAudit(AuditType.LOAN_REPAID, "Loan with ID " + loan.getId() + " has been fully repaid.", loan.getAccount().getId());
        } else {
            loan.setNextInstallmentDate(loan.getNextInstallmentDate().plusMonths(1));
        }

        repository.save(loan);
    }
}
