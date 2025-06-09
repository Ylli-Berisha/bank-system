package com.ylli.transactions_service.configs;

import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.enums.*;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import com.ylli.transactions_service.repositories.LoansRepository;
import com.ylli.transactions_service.repositories.TransactionsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner  {
    private final TransactionsRepository transactionsRepository;
    private final LoansRepository loansRepository;
    private final AccountsFeignClient accountsFeignClient;

    public DataInitializer(TransactionsRepository transactionsRepository, UsersFeignClient usersFeignClient, LoansRepository loansRepository, AccountsFeignClient accountsFeignClient) {
        this.transactionsRepository = transactionsRepository;
        this.loansRepository = loansRepository;
        this.accountsFeignClient = accountsFeignClient;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            var response = accountsFeignClient.getDefaultAccount();
            AccountDto accountDto = response != null ? response.getBody() : null;

            if (accountDto == null) {
                System.err.println("Default account not found. Skipping data initialization.");
                return;
            }

            if (transactionsRepository.count() == 0) {
                Account account = new Account();
                account.setId(accountDto.getId());

                Transaction transaction = new Transaction();
                transaction.setAccount(account);
                transaction.setType(TransactionType.DEPOSIT);
                transaction.setAmount(BigDecimal.valueOf(1000));
                transaction.setStatus(TransactionStatus.COMPLETED);

                transactionsRepository.save(transaction);

            }
            if (loansRepository.count() == 0) {
                Account account = new Account();
                account.setId(accountDto.getId());

                Loan loan = new Loan();
                loan.setAccount(account);
                loan.setAmount(5000.0);
                loan.setInterestRate(5.0);
                loan.setStatus(LoanStatus.ACTIVE);
                loan.setStartDate(java.time.LocalDate.now());
                loan.setEndDate(java.time.LocalDate.now().plusYears(1));

                loansRepository.save(loan);

            }
        } catch (Exception e) {
            System.err.println("Failed to initialize data: " + e.getMessage());
        }
    }


}
