package com.ylli.shared.clients;

import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.dtos.TransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "transactions-service", path = "/api")
public interface TransactionsFeignClient {

    @GetMapping("/transactions/get/{id}")
    ResponseEntity<TransactionDto> getTransaction(@PathVariable("id") String id);

    @PostMapping("/transactions/create")
    ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transaction);

    @PutMapping("/transactions/update/{id}")
    ResponseEntity<TransactionDto> updateTransaction(@PathVariable("id") String id, @RequestBody TransactionDto transaction);

    @DeleteMapping("/transactions/delete/{id}")
    ResponseEntity<Void> deleteTransaction(@PathVariable("id") String id);

    @GetMapping("/loans/get/{id}")
    ResponseEntity<LoanDto> getLoan(@PathVariable("id") Long id);

    @PostMapping("/loans/create")
    ResponseEntity<LoanDto> createLoan(@RequestBody LoanDto loan);

    @PutMapping("/loans/update/{id}")
    ResponseEntity<LoanDto> updateLoan(@PathVariable("id") Long id, @RequestBody LoanDto loan);

    @DeleteMapping("/loans/delete/{id}")
    ResponseEntity<Void> deleteLoan(@PathVariable("id") Long id);
}
