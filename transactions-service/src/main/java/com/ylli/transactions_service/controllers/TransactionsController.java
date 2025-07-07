package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.transactions_service.services.TransactionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Transactions", description = "Operations related to transactions")
@RestController
@RequestMapping("/api/transactions")
public class TransactionsController extends BaseController<TransactionDto, String, TransactionsService> {

    public TransactionsController(TransactionsService service) {
        super(service);
    }

    @Operation(summary = "Get user transactions", description = "Retrieve all transactions for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing user ID", content = @Content),
            @ApiResponse(responseCode = "204", description = "No transactions found", content = @Content)
    })
    @GetMapping("/get/user-transactions")
    public ResponseEntity<List<TransactionDto>> getUserTransactions(@RequestHeader("X-User-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<TransactionDto> transactions = service.getUserTransactions(userId);

        if (transactions == null || transactions.isEmpty()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }

        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Filter user transactions", description = "Filter transactions for a user based on optional criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions filtered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing user ID", content = @Content),
            @ApiResponse(responseCode = "204", description = "No matching transactions found", content = @Content)
    })
    @GetMapping("/filter/user-transactions")
    public ResponseEntity<List<TransactionDto>> filterUserTransactions(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String query
    ) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<TransactionDto> transactions = service.filterUserTransactions(
                userId, type, status, startDate, endDate, minAmount, maxAmount, query
        );

        if (transactions == null || transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Create new transaction", description = "Create a new transaction for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Related resource not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Transaction conflict occurred", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/create-new")
    public ResponseEntity<TransactionDto> createTransaction(
            @Validated @RequestBody TransactionDto transactionDto,
            @RequestHeader("X-User-ID") String userId
    ) {
        try {
            TransactionDto createdTransaction = service.createTransaction(transactionDto, userId);
            return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
