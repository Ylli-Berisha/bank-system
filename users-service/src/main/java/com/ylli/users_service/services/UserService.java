package com.ylli.users_service.services;
import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.*;
import com.ylli.users_service.dtos.UserLoginDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService extends BaseService<UserDto, String> {
    List<UserDto> getAllUsers();

    SignUpResponseDto signUp(UserSignUpDto userSignUpDto);

    LoginResponseDto login(UserLoginDto userLoginDto);

    UserDto getDefaultUser();

    Page<UserDto> filterAdminUsers(String adminId, UserFilterDto filterDto, int page, int size);
}
