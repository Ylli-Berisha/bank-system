package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.models.Transaction;
import com.ylli.transactions_service.mappers.TransactionMapper;
import com.ylli.transactions_service.repositories.TransactionsRepository;
import com.ylli.transactions_service.services.TransactionsService;
import org.springframework.stereotype.Service;

@Service
public class TransactionsServiceImpl extends BaseServiceImpl<Transaction, TransactionDto, String, TransactionsRepository, TransactionMapper> implements TransactionsService {
    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, TransactionMapper transactionMapper){
        super(transactionsRepository, transactionMapper);
    }
}
