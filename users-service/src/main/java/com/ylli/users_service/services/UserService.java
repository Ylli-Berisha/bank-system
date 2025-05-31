package com.ylli.users_service.services;
import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.LoginResponseDto;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.dtos.UserLoginDto;
import com.ylli.shared.dtos.UserSignUpDto;

import java.util.List;

public interface UserService extends BaseService<UserDto, String> {
    List<UserDto> getAllUsers();

    void signUp(UserSignUpDto userSignUpDto);

    LoginResponseDto login(UserLoginDto userLoginDto);

//    UserDto validateUser(String username);
}
