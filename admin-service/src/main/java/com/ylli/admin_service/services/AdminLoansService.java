package com.ylli.admin_service.services;

import com.ylli.shared.dtos.LoanDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface AdminLoansService {

    Page<LoanDto> getFilteredLoans(
            String adminId,
            String userId,
            String username,
            String email,
            String typeString,
            String statusString,
            String startDate,
            String endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page,
            int size
    );

}
