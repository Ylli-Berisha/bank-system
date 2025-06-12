package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.transactions_service.services.LoansService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(
        name = "Loans",
        description = "Operations related to loans"
)
@RestController
@RequestMapping("/api/loans")
public class LoansController extends BaseController<LoanDto, Long, LoansService> {
    public LoansController(LoansService service) {
        super(service);
    }

    @GetMapping("/get/user-loans")
    public ResponseEntity<List<LoanDto>> getUserLoans(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(required = false) LoanStatus status) {

        if (userId == null || userId.isBlank()) {
            System.err.println("Bad Request for fetching loans: X-User-ID header is missing or blank.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            List<LoanDto> loans = service.getUserLoans(userId, status);

            return ResponseEntity.ok(loans != null ? loans : Collections.emptyList());

        } catch (EntityNotFoundException e) {
            System.err.println("User or Account not found for ID: " + userId + ". Error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Internal Server Error during fetching user loans for ID: " + userId + ". Error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/loan-types")
    public ResponseEntity<List<String>> getLoanTypes() {
        try {
            List<String> loanTypes = service.getLoanTypes();
            if (loanTypes == null || loanTypes.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(loanTypes);
        } catch (Exception e) {
            System.err.println("Internal Server Error during fetching loan types. Error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyForNewLoan(
            @RequestParam("accountId") String accountId,
            @RequestBody LoanApplicationRequestDto requestDto,
            @RequestHeader("X-User-ID") String userId
    ) {
        if (accountId == null || accountId.isBlank() || requestDto == null || userId == null || userId.isBlank()) {
            System.err.println("Bad Request for loan application: Missing required parameters.");
            return new ResponseEntity<>("Missing required parameters.", HttpStatus.BAD_REQUEST);
        }

        try {
            service.applyForLoan(accountId, requestDto, userId);
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            System.err.println("Bad Request during loan application: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            System.err.println("Entity not found during loan application: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Internal Server Error during loan application: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
