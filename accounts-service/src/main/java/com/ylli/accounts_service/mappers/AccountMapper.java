package com.ylli.accounts_service.mappers;

import com.ylli.shared.SimpleMapper;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.models.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper extends SimpleMapper<Account, AccountDto> {
}
