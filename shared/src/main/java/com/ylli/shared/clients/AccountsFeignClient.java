package com.ylli.shared.clients;

import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.PageResponseDto;
import com.ylli.shared.fallback.AccountsFallbackImpl;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "accounts-service", url = "${accounts.service.url}", path = "/api/accounts", fallbackFactory = AccountsFallbackImpl.class)
//@FeignClient(name = "accounts-service", url = "http://localhost:8080", path = "/api/accounts", fallbackFactory = AccountsFallbackImpl.class)
public interface AccountsFeignClient {
    @GetMapping("/get/all")
    ResponseEntity<List<AccountDto>> getAll();

    @GetMapping("/get/{id}")
    ResponseEntity<AccountDto> getAccount(@PathVariable("id") String id);

    @PostMapping("/create")
    ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto account);

    @PutMapping("/update/{id}")
    ResponseEntity<AccountDto> updateAccount(@PathVariable("id") String id, @RequestBody AccountDto account);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteAccount(@PathVariable("id") String id);

    @GetMapping("/get/default-account")
    ResponseEntity<AccountDto> getDefaultAccount();

    @GetMapping("/get/user-accounts")
    ResponseEntity<Page<AccountDto>> getUserAccounts(@RequestHeader("X-User-ID") String userId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "6") Integer size);

    @GetMapping("/get/user-accounts2")
    ResponseEntity<List<AccountDto>> getUserAccounts2(@RequestHeader("X-User-Id") String userId);

    @GetMapping("/get/by-id-and-user-id")
    ResponseEntity<AccountDto> getAccountByIdAndUserId(
            @RequestParam String id,
            @RequestHeader("X-User-Id") String userId
    );

    @GetMapping("/get/{id}")
    ResponseEntity<AccountDto> getById(@PathVariable String id);

    @PutMapping("/update/{id}")
    ResponseEntity<AccountDto> update(@PathVariable String id, @Valid @RequestBody AccountDto dto);

    @PatchMapping("/{id}/freeze")
    ResponseEntity<?> freezeAccount(@PathVariable String id);

    @PatchMapping("/{id}/unfreeze")
    ResponseEntity<?> unfreezeAccount(@PathVariable String id);

    @PutMapping("/{id}/freeze-from-admin")
    ResponseEntity<?> freezeAccountFromAdmin(@PathVariable String id);

    @GetMapping("/filter/admin-accounts")
    ResponseEntity<PageResponseDto<AccountDto>> filterAdminAccounts(
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
    );
}

