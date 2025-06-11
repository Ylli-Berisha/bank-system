package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.enums.TransactionStatus;
import com.ylli.shared.enums.TransactionType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import com.ylli.transactions_service.configs.TransactionSpecifications;
import com.ylli.transactions_service.mappers.TransactionMapper;
import com.ylli.transactions_service.repositories.TransactionsRepository;
import com.ylli.transactions_service.services.TransactionsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionsServiceImpl extends BaseServiceImpl<Transaction, TransactionDto, String, TransactionsRepository, TransactionMapper> implements TransactionsService {
    private final AccountsFeignClient accountsFeignClient;

    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, TransactionMapper transactionMapper, AccountsFeignClient accountsFeignClient){
        super(transactionsRepository, transactionMapper);
        this.accountsFeignClient = accountsFeignClient;
    }

    @Override
    public List<TransactionDto> getUserTransactions(String userId) {
        List<AccountDto> accounts = accountsFeignClient.getUserAccounts(userId).getBody();

        List<Transaction> transactions = new ArrayList<>();
        if (accounts == null || accounts.isEmpty()) {
            throw new EntityNotFoundException("User with ID " + userId + " has no accounts.");
        }
        for (AccountDto account : accounts) {
            var tempAccount = new Account();
            tempAccount.setId(account.getId());
            transactions.addAll(repository.findByAccount(tempAccount));
        }

        if (transactions.isEmpty()) {
            return null;
        }
        return mapper.toDtoList(transactions);

    }

    @Override
    public List<TransactionDto> filterUserTransactions(
            String userId,
            String typeString,
            String statusString,
            String startDate,
            String endDate,
            BigDecimal minAmount, // These can be null from the frontend
            BigDecimal maxAmount, // These can be null from the frontend
            String query
    ) {
        // 1. Parse/Convert Parameters from Frontend
        LocalDateTime parsedStartDateTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            parsedStartDateTime = LocalDate.parse(startDate).atStartOfDay();
        }

        LocalDateTime parsedEndDateTime = null;
        if (endDate != null && !endDate.isEmpty()) {
            parsedEndDateTime = LocalDate.parse(endDate).atTime(23, 59, 59, 999999999);
        }

        TransactionStatus parsedStatus = null;
        if (statusString != null && !statusString.isEmpty()) {
            try {
                parsedStatus = TransactionStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid TransactionStatus string: " + statusString);
            }
        }

        TransactionType parsedType = null;
        if (typeString != null && !typeString.isEmpty()) {
            try {
                parsedType = TransactionType.valueOf(typeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid TransactionType string: " + typeString);
            }
        }

        // 2. Handle BigDecimal nulls: Ensure they are always non-null when passed to Specification
        BigDecimal actualMinAmount = (minAmount != null) ? minAmount : BigDecimal.ZERO;
        BigDecimal actualMaxAmount = (maxAmount != null) ? maxAmount : new BigDecimal("999999999999999.99");


        // 3. Build and Execute the Specification
        List<Transaction> transactions = repository.findAll(
                TransactionSpecifications.withFilters(
                        userId,
                        parsedType,
                        parsedStatus,
                        parsedStartDateTime,
                        parsedEndDateTime,
                        actualMinAmount,
                        actualMaxAmount,
                        query
                )
        );

        return mapper.toDtoList(transactions);
    }
}
