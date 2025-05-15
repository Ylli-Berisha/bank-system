package com.ylli.users_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDto> {
}
