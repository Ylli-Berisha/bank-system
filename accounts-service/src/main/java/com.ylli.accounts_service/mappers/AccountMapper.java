package com.ylli.accounts_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.models.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper extends BaseMapper<Account, AccountDto> {
}
