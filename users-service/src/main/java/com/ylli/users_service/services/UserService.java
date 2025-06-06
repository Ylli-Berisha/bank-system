package com.ylli.users_service.services;
import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.LoginResponseDto;
import com.ylli.shared.dtos.SignUpResponseDto;
import com.ylli.shared.dtos.UserDto;
import com.ylli.users_service.dtos.UserLoginDto;
import com.ylli.shared.dtos.UserSignUpDto;

import java.util.List;

public interface UserService extends BaseService<UserDto, String> {
    List<UserDto> getAllUsers();

    SignUpResponseDto signUp(UserSignUpDto userSignUpDto);

    LoginResponseDto login(UserLoginDto userLoginDto);

//    UserDto validateUser(String username);
}
