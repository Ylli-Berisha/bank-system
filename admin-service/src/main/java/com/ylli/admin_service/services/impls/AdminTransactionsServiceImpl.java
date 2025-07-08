package com.ylli.admin_service.services.impls;

import com.ylli.admin_service.services.AdminTransactionsService;
import com.ylli.shared.clients.TransactionsFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminTransactionsServiceImpl implements AdminTransactionsService {
    private final TransactionsFeignClient transactionsFeignClient;
//    private final UsersFeignClient usersFeignClient;

    public AdminTransactionsServiceImpl(TransactionsFeignClient transactionsFeignClient, UsersFeignClient usersFeignClient) {
        this.transactionsFeignClient = transactionsFeignClient;
//        this.usersFeignClient = usersFeignClient;
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
