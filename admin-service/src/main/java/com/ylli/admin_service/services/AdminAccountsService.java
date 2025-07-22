package com.ylli.admin_service.services;

import com.ylli.shared.dtos.AccountDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface AdminAccountsService {

    List<AccountDto> getAllAccounts(String userId);

    Boolean freezeAccount(String accountId, String userId);

    Boolean unfreezeAccount(String accountId, String userId);

    void approveAccount(String accountId, String userId);

    void rejectAccount(String accountId, String userId);

    Page<AccountDto> filterAdminAccounts(
            String adminId,
            String accountId,
            String typeString,
            BigDecimal minBalance,
            BigDecimal maxBalance,
            String statusString,
            String userId,
            String username,
            String email,
            String loanId,
            String transactionId,
            int page,
            int size
    );
}
