package com.ylli.admin_service.services;

import com.ylli.shared.dtos.UserDto;
import org.springframework.data.domain.Page;

public interface AdminUsersService {
    Page<UserDto> getAllUsers(String userId, int page, int size);

    Page<UserDto> getFilteredUsers(String userId, String id, String username, String firstName, String lastName,
                                   String email, String phoneNumber, Boolean isActive, String accountId,
                                   String loanId, String transactionId, int page, int size);
}
