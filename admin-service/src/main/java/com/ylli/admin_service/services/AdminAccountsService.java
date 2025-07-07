package com.ylli.admin_service.services;

import com.ylli.shared.dtos.AccountDto;

import java.util.List;

public interface AdminAccountsService {

    List<AccountDto> getAllAccounts(String userId);

    Boolean freezeAccount(String accountId, String userId);

    Boolean unfreezeAccount(String accountId, String userId);

    void approveAccount(String accountId, String userId);

    void rejectAccount(String accountId, String userId);
}
