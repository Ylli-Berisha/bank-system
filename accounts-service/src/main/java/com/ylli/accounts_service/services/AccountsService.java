package com.ylli.accounts_service.services;

import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.AccountDto;

import java.util.List;

public interface AccountsService extends BaseService<AccountDto, String> {

    List<AccountDto> getAll();

    List<AccountDto> getUserAccounts(String userId);

    AccountDto getDefaultAccount();

    List<String> getAccountTypes();

    List<String> getAccountStatuses();

    Boolean applyForNewAccount(AccountDto accountDto);

    Boolean freezeAccount(String accountId, String userId);

    Boolean unfreezeAccount(String accountId, String userId);

    AccountDto getByIdAndUserId(String id, String userId);

    Boolean validateAdmin(String userId);
}
