package com.ylli.accounts_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper extends BaseMapper<Account, AccountDto> {

    @Mapping(target = "userId", source = "user")
    @Override
    AccountDto toDto(Account account);

    @Mapping(target = "user", source = "userId")
    @Override
    Account toEntity(AccountDto accountDto);

    default User mapUserIdToUser(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        var user = new User();
        user.setId(userId);
        return user;
    }

    default String mapUserToUserId(User user) {
        return user != null ? user.getId() : null;
    }
}