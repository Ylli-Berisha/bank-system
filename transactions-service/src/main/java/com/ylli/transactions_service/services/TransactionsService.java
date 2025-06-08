package com.ylli.transactions_service.services;


import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.base.BaseService;

import java.util.List;

public interface TransactionsService extends BaseService<TransactionDto, String> {
    List<TransactionDto> getUserTransactions(String userId);
}
