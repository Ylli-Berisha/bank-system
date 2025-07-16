package com.ylli.admin_service.services.impls;

import com.ylli.admin_service.services.AdminLoansService;
import com.ylli.shared.clients.TransactionsFeignClient;
import com.ylli.shared.dtos.LoanDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;

@Service
public class AdminLoansServiceImpl implements AdminLoansService {

    private final TransactionsFeignClient transactionsFeignClient;

    public AdminLoansServiceImpl(TransactionsFeignClient transactionsFeignClient) {
        this.transactionsFeignClient = transactionsFeignClient;
    }

    @Override
    public Page<LoanDto> getFilteredLoans(String adminId, String userId, String username, String email, String typeString, String statusString, String startDate, String endDate, BigDecimal minAmount, BigDecimal maxAmount, int page, int size) {
        try {
            return transactionsFeignClient.filterAdminLoans( adminId,  userId,  username,  email,  typeString,  statusString,  startDate,  endDate,  minAmount,  maxAmount,
             page,  size
            ).getBody();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching transactions: " + e.getMessage(), e);
        }
    }
}
