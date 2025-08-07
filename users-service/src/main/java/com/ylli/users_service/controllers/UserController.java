package com.ylli.users_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.*;
import com.ylli.users_service.dtos.UserLoginDto;
import com.ylli.users_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Users", description = "Operations related to users")
@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<UserDto, String, UserService> {

    @Autowired
    public UserController(UserService userService) {
        super(userService);
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided signup information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already in use"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/auth/signup")
    public ResponseEntity<SignUpResponseDto> signUp(@Valid @RequestBody UserSignUpDto dto) {
        var responseDto = service.signUp(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "User login", description = "Authenticates a user with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserLoginDto loginDto) {
        log.info("login: {}", loginDto);
        LoginResponseDto user = service.login(loginDto);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get default user", description = "Fetches the default user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Default user fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Default user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get/default-user")
    public ResponseEntity<UserDto> getDefaultUser() {
        var user = service.getDefaultUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Filter admin users", description = "Filter users for admin panel based on optional criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users filtered successfully"),
            @ApiResponse(responseCode = "204", description = "No matching users found", content = @Content)
    })
    @GetMapping("/filter/admin-users")
    public ResponseEntity<Page<UserDto>> filterAdminUsers(
            @RequestHeader("X-User-ID") String adminId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String loanId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (adminId == null || adminId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setId(userId);
        filterDto.setUsername(username);
        filterDto.setEmail(email);
        filterDto.setFirstName(firstName);
        filterDto.setLastName(lastName);
        filterDto.setPhoneNumber(phoneNumber);
        filterDto.setIsActive(isActive);
        filterDto.setAccountId(accountId);
        filterDto.setLoanId(loanId);
        filterDto.setTransactionId(transactionId);

        Page<UserDto> usersPage = service.filterAdminUsers(adminId, filterDto, page, size);

        if (usersPage == null || usersPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(usersPage);
    }

    @Operation(summary = "Get all users", description = "Get all users endpoint (pageable) for admin actions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users returned successfully "),
            @ApiResponse(responseCode = "204", description = "No users found")
    })
    @GetMapping("/get/all-users")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestHeader("X-User-Id") String adminId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size

    ) {
        if (adminId == null || adminId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Page<UserDto> usersPage = service.getAllUsers(adminId, page, size);
        if (usersPage == null || usersPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usersPage);
    }

}
