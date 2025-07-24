package com.ylli.transactions_service.services;


import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.base.BaseService;
import org.springframework.data.domain.Page;

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

    Page<TransactionDto> filterAdminTransactions(
            String adminId,
            String userId,
            String username,
            String email,
            String type,
            String status,
            String startDate,
            String endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String query,
            int page,
            int size
    );

    TransactionDto createTransaction(TransactionDto transactionDto, String userId);

    TransactionDto revertTransaction(String transactionId, String adminId);

    List<TransactionDto> getTopUserTransactions(String userId);
}
