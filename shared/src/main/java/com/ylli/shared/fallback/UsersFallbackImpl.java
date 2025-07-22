package com.ylli.shared.fallback;

import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.UserDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UsersFallbackImpl implements FallbackFactory<UsersFeignClient> {
    @Override
    public UsersFeignClient create(Throwable cause) {
        return new UsersFeignClient() {

            @Override
            public ResponseEntity<UserDto> getUser(String id) {
                return ResponseEntity.status(503).build();

            }

            @Override
            public ResponseEntity<UserDto> createUser(UserDto user) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<UserDto> updateUser(String id, UserDto user) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<Void> deleteUser(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<UserDto> getDefaultUser() {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<Page<UserDto>> filterAdminUsers(String adminId, String userId, String username, String email, String firstName, String lastName, String phoneNumber, Boolean isActive, String accountId, String loanId, String transactionId, int page, int size) {
                return new ResponseEntity<>(Page.empty(), ResponseEntity.status(503).build().getStatusCode());
            }
        };
    }
}
