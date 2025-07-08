package com.ylli.admin_service.services;

import com.ylli.shared.dtos.TransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface AdminTransactionsService {

    Page<TransactionDto> getFilteredTransactions(
            String adminId,
            String userId,
            String username,
            String email,
            String startDate,
            String endDate,
            String type,
            String status,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String query,
            int page,
            int size
    );

}
