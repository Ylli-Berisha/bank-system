package com.ylli.shared.clients;

import com.ylli.shared.dtos.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "accounts-service", path = "/api/accounts")
public interface AccountsFeignClient {
    @GetMapping("/get/{id}")
    ResponseEntity<AccountDto> getAccount(@PathVariable("id") String id);

    @PostMapping("/create")
    ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto account);

    @PutMapping("/update/{id}")
    ResponseEntity<AccountDto> updateAccount(@PathVariable("id") String id, @RequestBody AccountDto account);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteAccount(@PathVariable("id") String id);
}

