package com.ylli.users_service.mappers;

import com.ylli.shared.SimpleMapper;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends SimpleMapper<User, UserDto> {
}
