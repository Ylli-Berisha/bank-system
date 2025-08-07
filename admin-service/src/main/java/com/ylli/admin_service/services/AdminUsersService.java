package com.ylli.admin_service.services;

import com.ylli.shared.dtos.UserDto;
import org.springframework.data.domain.Page;

public interface AdminUsersService {
    Page<UserDto> getAllUsers(String userId, int page, int size);
}
