package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import com.ylli.transactions_service.mappers.TransactionMapper;
import com.ylli.transactions_service.repositories.TransactionsRepository;
import com.ylli.transactions_service.services.TransactionsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

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
}
