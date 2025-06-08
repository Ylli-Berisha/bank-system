package com.ylli.transactions_service.configs;

import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AccountType;
import com.ylli.shared.enums.TransactionStatus;
import com.ylli.shared.enums.TransactionType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import com.ylli.transactions_service.repositories.TransactionsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner  {
    private final TransactionsRepository transactionsRepository;
    private final AccountsFeignClient accountsFeignClient;

    public DataInitializer(TransactionsRepository transactionsRepository, UsersFeignClient usersFeignClient, AccountsFeignClient accountsFeignClient) {
        this.transactionsRepository = transactionsRepository;
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
        } catch (Exception e) {
            System.err.println("Failed to initialize data: " + e.getMessage());
        }
    }


}
