package com.ylli.accounts_service.services;

import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.models.Account;

public interface AccountsService {
    AccountDto getAccount(String accountId);
    AccountDto createAccount(AccountDto account);
    AccountDto updateAccount(AccountDto account);
    AccountDto deleteAccount(String accountId);
}
