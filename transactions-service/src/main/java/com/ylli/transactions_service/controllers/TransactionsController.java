package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.transactions_service.services.TransactionsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.notFound().build();
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


}
