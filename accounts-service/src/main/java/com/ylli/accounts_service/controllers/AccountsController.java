package com.ylli.accounts_service.controllers;

import com.ylli.accounts_service.services.AccountsService;
import com.ylli.shared.dtos.AccountDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/")
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @GetMapping("get/{id}")
    public AccountDto getAccount(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        return accountsService.getAccount(id);
    }

    @PostMapping("create")
    public AccountDto createAccount(@Valid @RequestBody AccountDto accountDto) {
        return accountsService.createAccount(accountDto);
    }

    @PutMapping("update/{id}")
    public AccountDto updateAccount(@PathVariable String id, @Valid @RequestBody AccountDto accountDto) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        accountDto.setId(id);
        return accountsService.updateAccount(accountDto);
    }

    @DeleteMapping("delete/{id}")
    public AccountDto deleteAccount(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        return accountsService.deleteAccount(id);
    }
}