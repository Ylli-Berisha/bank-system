package com.ylli.shared.clients;

import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.fallback.UsersFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "users-service",url = "http://localhost:8120", path = "/api/users", fallbackFactory = UsersFallbackImpl.class)
public interface UsersFeignClient {

    @GetMapping("/get/{id}")
    ResponseEntity<UserDto> getUser(@PathVariable("id") String id);

    @PostMapping("/create")
    ResponseEntity<UserDto> createUser(@RequestBody UserDto user);

    @PutMapping("/update/{id}")
    ResponseEntity<UserDto> updateUser(@PathVariable("id") String id, @RequestBody UserDto user);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable("id") String id);

    @GetMapping("/get/default-user")
    ResponseEntity<UserDto> getDefaultUser();
}
