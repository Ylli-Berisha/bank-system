package com.ylli.admin_service.services;

import com.ylli.shared.dtos.AccountDto;

import java.util.List;

public interface AdminAccountsService {

    List<AccountDto> getAllAccounts();

    Boolean freezeAccount(String accountId);

    Boolean unfreezeAccount(String accountId);

    void approveAccount(String accountId);
}
