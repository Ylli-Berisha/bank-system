package com.ylli.users_service.controllers;

import com.ylli.shared.dtos.UserDto;
import com.ylli.users_service.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/")
//@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable @NotBlank(message = "ID cannot be blank") String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("create")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable @NotBlank(message = "ID cannot be blank") String id,
            @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable @NotBlank(message = "ID cannot be blank") String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}