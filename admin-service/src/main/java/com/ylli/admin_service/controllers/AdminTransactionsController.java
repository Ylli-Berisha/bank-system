package com.ylli.admin_service.controllers;

import com.ylli.admin_service.services.AdminTransactionsService;
import com.ylli.shared.dtos.TransactionDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Admin Transactions", description = "Administrative operations for managing transactions")
@RestController
@RequestMapping("/api/transactions")
public class AdminTransactionsController {

    private final AdminTransactionsService adminTransactionsService;

    public AdminTransactionsController(AdminTransactionsService adminTransactionsService) {
        this.adminTransactionsService = adminTransactionsService;
    }

    @GetMapping("/filter/transactions")
    public ResponseEntity<Page<TransactionDto>> getFilteredTransactions(
            @RequestHeader("X-User-ID") String adminId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<TransactionDto> transactions = adminTransactionsService.getFilteredTransactions(
                adminId, userId, username, email, startDate, endDate, type, status, minAmount, maxAmount,
                query, page, size
        );
        return ResponseEntity.ok(transactions);
    }


}
