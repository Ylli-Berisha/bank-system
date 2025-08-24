package com.ylli.admin_service.controllers;

import com.ylli.admin_service.services.AdminTransactionsService;
import com.ylli.shared.dtos.TransactionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get filtered transactions", description = "Retrieve transactions based on various filter criteria")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"), @ApiResponse(responseCode = "400", description = "Invalid filter parameters"), @ApiResponse(responseCode = "404", description = "No transactions found matching the criteria"), @ApiResponse(responseCode = "500", description = "Internal server error while retrieving transactions")})
    @GetMapping("/filter/transactions")
    public ResponseEntity<Page<TransactionDto>> getFilteredTransactions(@RequestHeader("X-User-ID") String adminId, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) String type, @RequestParam(required = false) String status, @RequestParam(required = false) BigDecimal minAmount, @RequestParam(required = false) BigDecimal maxAmount, @RequestParam(required = false) String userId, @RequestParam(required = false) String username, @RequestParam(required = false) String email, @RequestParam(required = false) String query, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
        Page<TransactionDto> transactions = adminTransactionsService.getFilteredTransactions(adminId, userId, username, email, startDate, endDate, type, status, minAmount, maxAmount, query, page, size);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Revert a transaction", description = "Reverts a transaction by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Transaction reverted successfully"), @ApiResponse(responseCode = "400", description = "Invalid transaction ID or admin ID"), @ApiResponse(responseCode = "404", description = "Transaction not found"), @ApiResponse(responseCode = "500", description = "Internal server error while reverting transaction")})
    @PutMapping("/revert")
    public ResponseEntity<TransactionDto> revertTransaction(@RequestParam String transactionId, @RequestHeader("X-User-ID") String adminId) {
        TransactionDto revertedTransaction = adminTransactionsService.revertTransaction(transactionId, adminId);
        return ResponseEntity.ok(revertedTransaction);

    }


}
