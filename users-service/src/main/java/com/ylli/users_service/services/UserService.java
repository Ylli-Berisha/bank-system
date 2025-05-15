package com.ylli.users_service.services;
import com.ylli.shared.dtos.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(String id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(String id, UserDto userDto);

    UserDto deleteUser(String id);

    List<UserDto> getAllUsers();
}
