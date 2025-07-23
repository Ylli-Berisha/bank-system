package com.ylli.admin_service.services.impls;

import com.ylli.admin_service.services.AdminTransactionsService;
import com.ylli.shared.base.AuditHelper;
import com.ylli.shared.clients.TransactionsFeignClient;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.enums.AuditType;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminTransactionsServiceImpl implements AdminTransactionsService {
    private final TransactionsFeignClient transactionsFeignClient;
    private final AuditHelper auditHelper;

    public AdminTransactionsServiceImpl(TransactionsFeignClient transactionsFeignClient, AuditHelper auditHelper) {
        this.transactionsFeignClient = transactionsFeignClient;
        this.auditHelper = auditHelper;
    }

    @Override
    public Page<TransactionDto> getFilteredTransactions(String adminId, String userId, String username, String email, String startDate, String endDate, String type, String status, BigDecimal minAmount, BigDecimal maxAmount, String query, int page, int size) {
        try {
            return transactionsFeignClient.filterAdminTransactions(
                    adminId, userId, username, email,type, status, startDate, endDate,
                    minAmount, maxAmount, query, page, size
            ).getBody();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public TransactionDto revertTransaction(String transactionId, String adminId) {
        try {
            TransactionDto transactionDto = transactionsFeignClient.revertTransaction(transactionId, adminId).getBody();
            if (transactionDto == null) {
                throw new ResourceNotFoundException("Transaction with ID " + transactionId + " not found.");
            }

            auditHelper.createAudit(AuditType.TRANSACTION_REVERTED,
                    "Transaction with ID " + transactionId + " has been reverted by admin with ID " + adminId,
                    transactionDto.getAccountId());

            return transactionDto;

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while reverting transaction: " + e.getMessage(), e);
        }
    }

//    public void validateAdmin(String adminId) {
//        var user = usersFeignClient.getUser(adminId).getBody();
//        if (user.getRoles() == null || user.getRoles().isEmpty()) {
//            throw new IllegalArgumentException("User with ID " + adminId + " does not have any roles");
//        }
//        if (!user.getRoles().contains(UserRole.ROLE_ADMIN)) {
//            throw new IllegalArgumentException("User with ID " + adminId + " is not an admin");
//        }
//    }
}
