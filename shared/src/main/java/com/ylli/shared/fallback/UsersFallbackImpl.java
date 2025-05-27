package com.ylli.shared.fallback;

import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.UserDto;
import org.springframework.cloud.openfeign.FallbackFactory;
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
        };
    }
}
