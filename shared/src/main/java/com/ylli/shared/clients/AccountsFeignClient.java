package com.ylli.shared.clients;

import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.fallback.AccountsFallbackImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "accounts-service", url = "http://localhost:8080", path = "/api/accounts", fallbackFactory = AccountsFallbackImpl.class)
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
    ResponseEntity<List<AccountDto>> getUserAccounts(@RequestParam String userId);

    @GetMapping("/get/by-id-and-user-id")
    ResponseEntity<AccountDto> getAccountByIdAndUserId(
            @RequestParam String id,
            @RequestParam String userId
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
}

