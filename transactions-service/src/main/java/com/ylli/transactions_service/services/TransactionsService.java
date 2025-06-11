package com.ylli.transactions_service.services;


import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.base.BaseService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionsService extends BaseService<TransactionDto, String> {
    List<TransactionDto> getUserTransactions(String userId);
    List<TransactionDto> filterUserTransactions(
            String userId,
            String type,
            String status,
            String startDate,
            String endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String query
    );
}
