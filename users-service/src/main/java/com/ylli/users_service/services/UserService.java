package com.ylli.users_service.services;
import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.UserDto;

import java.util.List;

public interface UserService extends BaseService<UserDto, String> {
    List<UserDto> getAllUsers();
}
