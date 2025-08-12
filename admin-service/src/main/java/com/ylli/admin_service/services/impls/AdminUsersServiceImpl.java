package com.ylli.admin_service.services.impls;

import com.ylli.admin_service.services.AdminUsersService;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.UserDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUsersServiceImpl implements AdminUsersService {
    private final UsersFeignClient usersFeignClient;

    @Override
    public Page<UserDto> getAllUsers(String userId, int page, int size) {
        try{
            Page<UserDto> userDtos = usersFeignClient.getAllUsers(userId, page, size).getBody();
            return userDtos;
        }catch (FeignException e){
            log.error("Feign error when query users by userId:{}", userId);
            throw e;
        }
    }

    @Override
    public Page<UserDto> getFilteredUsers(String userId, String id, String username, String firstName, String lastName, String email, String phoneNumber, Boolean isActive, String accountId, String loanId, String transactionId, int page, int size) {
        try {
            Page<UserDto> userDtos = usersFeignClient.filterAdminUsers(userId, id, username, firstName, lastName, email, phoneNumber, isActive, accountId, loanId, transactionId, page, size).getBody();
            return userDtos;
        } catch (FeignException e) {
            log.error("Feign error when filtering users by userId:{}", userId);
            throw e;
        }
    }


}
