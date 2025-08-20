package com.ylli.accounts_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.accounts_service.services.AccountsService;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.PageResponseDto;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AccountType;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Tag(
        name = "Accounts",
        description = "Operations related to accounts"
)
@RestController
@RequestMapping("/api/accounts")
public class AccountsController extends BaseController<AccountDto, String, AccountsService> {

    private static final Logger log = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    public AccountsController(AccountsService accountsService) {
        super(accountsService);
    }

    @Operation(summary = "Get all accounts", description = "Retrieve all accounts from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No accounts found", content = @Content)
    })
    @GetMapping("/get/all")
    public ResponseEntity<List<AccountDto>> getAll() {
        List<AccountDto> accounts = service.getAll();
        if (accounts == null || accounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Get accounts by user ID", description = "Retrieve all accounts associated with a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No accounts found for user"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content)
    })
    @GetMapping("/get/user-accounts")
    public ResponseEntity<Page<AccountDto>> getUserAccounts(@RequestHeader("X-User-ID") String userId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "6") Integer size) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        Page<AccountDto> accounts = service.getUserAccounts(userId, page, size);
        if (accounts.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/get/user-accounts2")
    public ResponseEntity<List<AccountDto>> getUserAccounts2(@RequestHeader("X-User-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        List<AccountDto> accounts = service.getUserAccounts2(userId);
        return ResponseEntity.ok(accounts);
    }

//    @Operation(summary = "Get default account", description = "Retrieve the default account of the currently authenticated user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Default account retrieved successfully"),
//            @ApiResponse(responseCode = "404", description = "Default account not found", content = @Content)
//    })
//    @GetMapping("/get/default-account")
//    public ResponseEntity<AccountDto> getDefaultAccount() {
//        var account = service.getDefaultAccount();
//        if (account == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(account);
//    }

    @Operation(summary = "Get available account types", description = "Retrieve a list of supported account types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account types retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No account types found", content = @Content)
    })
    @GetMapping("/get/account-types")
    public ResponseEntity<List<String>> getAccountTypes() {
        List<String> accountTypes = service.getAccountTypes();
        if (accountTypes == null || accountTypes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accountTypes);
    }

    @Operation(summary = "Apply for a new account", description = "Submit a request to apply for a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account application submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account data or status", content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to process the application", content = @Content)
    })
    @PostMapping("/apply-for-account")
    public ResponseEntity<Void> applyForNewAccount(@RequestBody AccountDto accountDto) {
        if (accountDto == null || accountDto.getUserId().isBlank() || accountDto.getStatus() != AccountStatus.PENDING_APPROVAL) {
            return ResponseEntity.badRequest().build();
        }
        Boolean bool = service.applyForNewAccount(accountDto);
        if (bool == Boolean.FALSE) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Freeze an account", description = "Freeze a specific account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account frozen successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Account already frozen", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}/freeze")
    public ResponseEntity<?> freezeAccount(@PathVariable String id, @RequestHeader("X-User-ID") String userId) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("Account ID must be provided.");
        }

        try {
            boolean success = service.freezeAccount(id, userId);
            if (!success) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Account already frozen.");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error.");
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unfreeze an account", description = "Unfreeze a specific account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account unfrozen successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Account is not frozen", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}/unfreeze")
    public ResponseEntity<?> unfreezeAccount(@PathVariable String id, @RequestHeader("X-User-ID") String userId) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("Account ID must be provided.");
        }

        try {
            boolean success = service.unfreezeAccount(id, userId);
            if (!success) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Account is not frozen.");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error.");
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get account by ID and user ID", description = "Retrieve an account using its ID and the user ID that owns it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID or user ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/get/by-id-and-user-id")
    public ResponseEntity<AccountDto> getAccountByIdAndUserId(
            @RequestParam String id,
            @RequestHeader("X-User-ID") String userId
    ) {
        if (id == null || id.isEmpty() || userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AccountDto account;
        try {
            account = service.getByIdAndUserId(id, userId);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Get top user accounts", description = "Get the top accounts of the logged in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top accounts retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content)
    })
    @GetMapping("/get/top-accounts")
    public ResponseEntity<List<AccountDto>> getTopAccounts(@RequestHeader("X-User-ID") String userId) {
        System.out.println("Received request to get top accounts for user ID: " + userId);
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        List<AccountDto> topAccounts = Optional.ofNullable(service.getTopAccounts(userId)).orElse(Collections.emptyList());
        return ResponseEntity.ok(topAccounts);
    }

    @Operation(summary = "Filter admin accounts", description = "Filter accounts for admin panel based on optional criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts filtered successfully"),
            @ApiResponse(responseCode = "204", description = "No matching accounts found", content = @Content)
    })
    @GetMapping("/filter/admin-accounts")
    public ResponseEntity<PageResponseDto<AccountDto>> filterAdminAccounts(
            @RequestHeader("X-User-ID") String adminId,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String loanId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (adminId == null || adminId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        PageResponseDto<AccountDto> accounts = service.filterAdminAccounts(
                adminId,
                accountId,
                type,
                minBalance,
                maxBalance,
                status,
                userId,
                username,
                email,
                loanId,
                transactionId,
                page,
                size
        );

        if (accounts == null || accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Filter user accounts", description = "Filter accounts for a user based on optional criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts filtered successfully"),
            @ApiResponse(responseCode = "204", description = "No matching accounts found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "User id does not match the account queried")
    })
    @GetMapping("/filter/user-accounts")
    public ResponseEntity<Page<AccountDto>> filterUserAccounts(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        log.info("Received request to filter user accounts for user ID: " + userId);
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        AccountType parsedType = null;
        if (type != null && !type.isEmpty()) {
            try {
                parsedType = AccountType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        AccountStatus parsedStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                parsedStatus = AccountStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        Page<AccountDto> accounts = service.filterUserAccounts(
                accountId,
                userId,
                parsedType,
                minBalance,
                maxBalance,
                parsedStatus,
                page,
                size
        );

        if (accounts == null || accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(accounts);
    }




    @GetMapping("/get/by-status")
    @Operation(summary = "Get accounts by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No accounts found for the given status", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid status or parameters", content = @Content)})
    public ResponseEntity<PageResponseDto<AccountDto>> getAccountsByStatus(@RequestHeader("X-User-Id") String userId, @RequestParam String status, int page, int size) {
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        AccountStatus accountStatus;
        try {
            accountStatus = AccountStatus.valueOf(status.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
        PageResponseDto<AccountDto> accounts = service.getByStatus(userId, accountStatus, page, size);
        if (accounts == null || accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(accounts);
    }

//    @GetMapping("/get/account-statuses")
//    @Operation(summary = "Get all account statuses")
//    public ResponseEntity<List<String>> getAccountStatuses() {
//        List<String> accountStatuses = service.getAccountStatuses();
//        if (accountStatuses == null || accountStatuses.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(accountStatuses);
//    }


}