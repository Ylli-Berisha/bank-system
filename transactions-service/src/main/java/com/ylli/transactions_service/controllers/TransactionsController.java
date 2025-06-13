package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.transactions_service.services.TransactionsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(
        name = "Transactions",
        description = "Operations related to transactions"
)
@RestController
@RequestMapping("/api/transactions")
public class TransactionsController extends BaseController<TransactionDto, String, TransactionsService> {

    public TransactionsController(TransactionsService service) {
        super(service);
    }

    @GetMapping("/get/user-transactions")
    public ResponseEntity<List<TransactionDto>> getUserTransactions(@RequestParam String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<TransactionDto> transactions = service.getUserTransactions(userId);

        if (transactions == null || transactions.isEmpty()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/filter/user-transactions")
    public ResponseEntity<List<TransactionDto>> filterUserTransactions(
            @RequestParam String userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false)BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String query
    ){
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



    @PostMapping("/create-new")
    public ResponseEntity<TransactionDto> createTransaction(
            @Validated @RequestBody TransactionDto transactionDto,
            @RequestHeader("X-User-ID") String userId
    ) {
        try {
            TransactionDto createdTransaction = service.createTransaction(transactionDto, userId);
            return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Bad Request for transaction creation: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            System.err.println("Conflict during transaction creation: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            System.err.println("Entity not found during transaction creation: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Internal Server Error during transaction creation: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
