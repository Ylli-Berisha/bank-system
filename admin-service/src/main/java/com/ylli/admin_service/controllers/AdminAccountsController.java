package com.ylli.admin_service.controllers;

import com.ylli.admin_service.services.AdminAccountsService;
import com.ylli.shared.dtos.AccountDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Accounts", description = "Administrative operations for managing accounts")
@RestController
@RequestMapping("/api/accounts")
public class AdminAccountsController {

    private final AdminAccountsService adminAccountsService;

    @Autowired
    public AdminAccountsController(AdminAccountsService adminAccountsService) {
        this.adminAccountsService = adminAccountsService;
    }

    @Operation(summary = "Get all accounts (Admin)", description = "Retrieve all accounts in the system (Admin privilege required)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No accounts found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get/all")
    public ResponseEntity<?> getAllAccounts(@RequestHeader("X-User-ID") String userId) {
        try {
            List<AccountDto> accounts = adminAccountsService.getAllAccounts(userId);
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>("An internal server error occurred while fetching accounts.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Approve account", description = "Approve a pending account using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account approved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error while approving account", content = @Content)
    })
    @PatchMapping("/approve/account/{id}")
    public ResponseEntity<?> approveAccount(@PathVariable String id, @RequestHeader("X-User-ID") String userId) {
        try {
            adminAccountsService.approveAccount(id, userId);
        } catch (IllegalArgumentException e) {
            System.err.println("Error approving account: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while approving the account: " + e.getMessage());
            return new ResponseEntity<>("An internal server error occurred while approving the account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reject account", description = "Reject a pending account using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error while rejecting account", content = @Content)
    })
    @PatchMapping("/reject/account/{id}")
    public ResponseEntity<?> rejectAccount(@PathVariable String id, @RequestHeader("X-User-ID") String userId) {
        try {
            adminAccountsService.rejectAccount(id, userId);
        } catch (IllegalArgumentException e) {
            System.err.println("Error rejecting account: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while rejecting the account: " + e.getMessage());
            return new ResponseEntity<>("An internal server error occurred while rejecting the account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }


    /*
    @Operation(summary = "Freeze an account (Admin)", description = "Freeze an account by ID (Admin privilege required)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account frozen successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to freeze account", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}/freeze")
    public ResponseEntity<?> freezeAccount(@PathVariable String id) {
        try {
            boolean result = adminAccountsService.freezeAccount(id);
            if (result) {
                return new ResponseEntity<>("Account frozen successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to freeze account.", HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error freezing account: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while freezing the account: " + e.getMessage());
            return new ResponseEntity<>("An internal server error occurred while freezing the account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Unfreeze an account (Admin)", description = "Unfreeze an account by ID (Admin privilege required)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account unfrozen successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to unfreeze account", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}/unfreeze")
    public ResponseEntity<?> unfreezeAccount(@PathVariable String id) {
        try {
            boolean result = adminAccountsService.unfreezeAccount(id);
            if (result) {
                return new ResponseEntity<>("Account unfrozen successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to unfreeze account.", HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error unfreezing account: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while unfreezing the account: " + e.getMessage());
            return new ResponseEntity<>("An internal server error occurred while unfreezing the account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */
}
