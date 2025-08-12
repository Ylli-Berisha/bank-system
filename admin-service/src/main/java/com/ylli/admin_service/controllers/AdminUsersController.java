package com.ylli.admin_service.controllers;

import com.ylli.admin_service.services.AdminUsersService;
import com.ylli.shared.dtos.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Admin Users", description = "Administrative operations for managing users")
@RequiredArgsConstructor
public class AdminUsersController {
    private final AdminUsersService service;

    @Operation(summary = "Get all users", description = "Get all users endpoint (pageable) for admin actions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users returned successfully "),
            @ApiResponse(responseCode = "204", description = "No users found")
    })
    @GetMapping("/get/all")
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

        Page<UserDto> usersPage = service.getFilteredUsers(adminId, userId, username, email, firstName, lastName,
                phoneNumber, isActive, accountId, loanId, transactionId, page, size);
        if (usersPage == null || usersPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usersPage);
    }
}
