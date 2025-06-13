package com.ylli.accounts_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.accounts_service.services.AccountsService;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Accounts",
        description = "Operations related to accounts"
)
@RestController
@RequestMapping("/api/accounts")
public class AccountsController extends BaseController<AccountDto, String, AccountsService> {

    @Autowired
    public AccountsController(AccountsService accountsService) {
        super(accountsService);
    }

    @GetMapping("/get/all")
    @Operation(summary = "Get all accounts")
    public ResponseEntity<List<AccountDto>> getAll() {
        List<AccountDto> accounts = service.getAll();
        if (accounts == null || accounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/get/user-accounts")
    public ResponseEntity<List<AccountDto>> getUserAccounts(@RequestParam String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        List<AccountDto> accounts = service.getUserAccounts(userId);
        if (accounts == null || accounts.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/get/default-account")
    public ResponseEntity<AccountDto> getDefaultAccount() {
        var account = service.getDefaultAccount();
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @GetMapping("/get/account-types")
    @Operation(summary = "Get all account types")
    public ResponseEntity<List<String>> getAccountTypes() {
        List<String> accountTypes = service.getAccountTypes();
        if (accountTypes == null || accountTypes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accountTypes);
    }

    @PostMapping("/apply-for-account")
    @Operation(summary = "Apply for a new account")
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

    @PatchMapping("/{id}/freeze")
    @Operation(summary = "Freeze an account")
    public ResponseEntity<?> freezeAccount(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("Account ID must be provided.");
        }

        try {
            boolean success = service.freezeAccount(id);
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
//
//    @PutMapping("/{id}/freeze-from-admin")
//    @Operation(summary = "Freeze an account from admin")
//    public ResponseEntity<?> freezeAccountFromAdmin(@PathVariable String id, @RequestHeader("X-User-ID") String userId) {
//        service.validateAdmin(userId);
//        if (id == null || id.isEmpty()) {
//            return ResponseEntity.badRequest().body("Account ID must be provided.");
//        }
//
//        try {
//            boolean success = service.freezeAccount(id);
//            if (!success) {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("Account already frozen.");
//            }
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error.");
//        }
//        return ResponseEntity.ok().build();
//    }

    @PatchMapping("/{id}/unfreeze")
    @Operation(summary = "Unfreeze an account")
    public ResponseEntity<?> unfreezeAccount(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("Account ID must be provided.");
        }

        try {
            boolean success = service.unfreezeAccount(id);
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

    @GetMapping("/get/by-id-and-user-id")
    @Operation(summary = "Get account by ID and user ID")
    public ResponseEntity<AccountDto> getAccountByIdAndUserId(
            @RequestParam String id,
            @RequestParam String userId
    ) {
        if (id == null || id.isEmpty() || userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AccountDto account;
        try {
            account = service.getByIdAndUserId(id, userId);
        }catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(account);
    }




//    @GetMapping("/get/by-status")
//    @Operation(summary = "Get accounts by status")
//    public ResponseEntity<List<AccountDto>> getAccountsByStatus(@RequestParam String status) {
//        if (status == null) {
//            return ResponseEntity.badRequest().build();
//        }
//        AccountStatus accountStatus;
//        try {
//            accountStatus = AccountStatus.valueOf(status.toUpperCase());
//        }
//        catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//        List<AccountDto> accounts = service.getByStatus(accountStatus);
//        if (accounts == null || accounts.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(accounts);
//    }

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


//@RestController
//@RequestMapping("/api/accounts/")
//public class AccountsController {
//
//    @Autowired
//    private AccountsService accountsService;
//
//    @GetMapping("get/{id}")
//    public AccountDto getAccount(@PathVariable String id) {
//        if (id == null || id.isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//        return accountsService.getById(id);
//    }
//
//    @PostMapping("create")
//    public AccountDto createAccount(@Valid @RequestBody AccountDto accountDto) {
//        return accountsService.create(accountDto);
//    }
//
//    @PutMapping("update/{id}")
//    public AccountDto updateAccount(@PathVariable String id, @Valid @RequestBody AccountDto accountDto) {
//        if (id == null || id.isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//        return accountsService.update(id, accountDto);
//    }
//
//    @DeleteMapping("delete/{id}")
//    public AccountDto deleteAccount(@PathVariable String id) {
//        if (id == null || id.isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//        return accountsService.delete(id);
//    }
//}