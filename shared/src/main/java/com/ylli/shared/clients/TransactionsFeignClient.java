package com.ylli.shared.clients;

import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.fallback.TransactionsFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "transactions-service", url = "http://localhost:8110", path = "/api", fallbackFactory = TransactionsFallbackImpl.class)
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

    @GetMapping("/transactions/filter/admin-transactions")
    ResponseEntity<Page<TransactionDto>> filterAdminTransactions(
            @RequestHeader("X-User-ID") String adminId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size
    );

    @PutMapping("/transactions/admin/revert")
    ResponseEntity<TransactionDto> revertTransaction(
            @RequestParam String transactionId,
            @RequestHeader("X-User-ID") String adminId
    );

    @GetMapping("/loans/filter/admin-loans")
    ResponseEntity<Page<LoanDto>> filterAdminLoans(
            @RequestHeader("X-User-ID") String adminId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );
}
