package com.ylli.accounts_service.services;

import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.AccountDto;

import java.util.List;

public interface AccountsService extends BaseService<AccountDto, String> {

    List<AccountDto> getUserAccounts(String userId);
}
