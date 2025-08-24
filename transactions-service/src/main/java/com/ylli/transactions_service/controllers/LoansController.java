package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.exceptions.ResourceDoesNotMatchException;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import com.ylli.shared.models.Loan;
import com.ylli.transactions_service.services.LoansService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Slf4j
@Tag(name = "Loans", description = "Operations related to loans")
@RestController
@RequestMapping("/api/loans")
public class LoansController extends BaseController<LoanDto, Long, LoansService> {

    public LoansController(LoansService service) {
        super(service);
    }

    @Operation(summary = "Get loans for user (paginated)", description = "Retrieve a paginated list of loans for the authenticated user, optionally filtered by status")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Loans retrieved successfully"), @ApiResponse(responseCode = "400", description = "Missing or invalid user ID", content = @Content), @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    @GetMapping("/get/user-loans")
    public ResponseEntity<Page<LoanDto>> getUserLoans(@RequestHeader("X-User-ID") String userId, @RequestParam(required = false) LoanStatus status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) {

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Page<LoanDto> loans = service.getUserLoans(userId, status, page, size);
        loans.stream().forEach(loanDto -> {
            log.info("loanDto: {}", loanDto);
        });
        return ResponseEntity.ok(loans);
    }


    @Operation(summary = "Get available loan types", description = "Retrieve all loan types supported by the system")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Loan types retrieved successfully"), @ApiResponse(responseCode = "404", description = "No loan types found", content = @Content), @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    @GetMapping("/get/loan-types")
    public ResponseEntity<List<String>> getLoanTypes() {
        List<String> loanTypes = service.getLoanTypes();
        if (loanTypes == null || loanTypes.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(loanTypes);
    }

    @Operation(summary = "Apply for a loan", description = "Submit a new loan application for the authenticated user")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Loan application submitted successfully"), @ApiResponse(responseCode = "400", description = "Invalid input data or missing parameters", content = @Content), @ApiResponse(responseCode = "404", description = "Associated account not found", content = @Content), @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    @PostMapping("/apply")
    public ResponseEntity<?> applyForNewLoan(@RequestParam("accountId") String accountId, @RequestBody LoanApplicationRequestDto requestDto, @RequestHeader("X-User-ID") String userId) {
        if (accountId == null || accountId.isBlank() || requestDto == null || userId == null || userId.isBlank()) {
            return new ResponseEntity<>("Missing required parameters.", HttpStatus.BAD_REQUEST);
        }

        service.applyForLoan(accountId, requestDto, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Filter user loans", description = "Filter loans for a user based on multiple optional criteria")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Loans filtered successfully"), @ApiResponse(responseCode = "400", description = "Invalid or missing user ID", content = @Content), @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    @GetMapping("/filter/user-loans")
    public ResponseEntity<List<LoanDto>> getFilteredLoans(@RequestHeader("X-User-ID") String userId, @RequestParam(name = "loanType", required = false) String loanType, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "startDate", required = false) String startDate, @RequestParam(name = "endDate", required = false) String endDate, @RequestParam(name = "minAmount", required = false) Double minAmount, @RequestParam(name = "maxAmount", required = false) Double maxAmount, @RequestParam(name = "query", required = false) String query) {
        List<LoanDto> filteredLoans = service.filterUserLoans(userId, loanType, status, startDate, endDate, minAmount, maxAmount, query);
        return ResponseEntity.ok(filteredLoans);
    }

    @Operation(summary = "Filter admin loans", description = "Filter loans for admin panel based on optional criteria")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Loans filtered successfully"), @ApiResponse(responseCode = "204", description = "No matching loans found", content = @Content)})
    @GetMapping("/filter/admin-loans")
    public ResponseEntity<Page<LoanDto>> filterAdminLoans(@RequestHeader("X-User-ID") String adminId, @RequestParam(required = false) String userId, @RequestParam(required = false) String username, @RequestParam(required = false) String email, @RequestParam(required = false) String type, @RequestParam(required = false) String status, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) BigDecimal minAmount, @RequestParam(required = false) BigDecimal maxAmount, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
        if (adminId == null || adminId.isEmpty() || page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Page<LoanDto> loans = service.filterAdminLoans(adminId, userId, username, email, type, status, startDate, endDate, minAmount, maxAmount, page, size);

        if (loans == null || loans.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(loans);
    }

    @Operation(summary = "Accept a pending loan", description = "Accepts a pending loan application by admin")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Loan accepted successfully"), @ApiResponse(responseCode = "400", description = "Invalid input or loan cannot be accepted"), @ApiResponse(responseCode = "404", description = "Loan not found"), @ApiResponse(responseCode = "403", description = "Admin validation failed")})
    @PutMapping("/{loanId}/accept")
    public ResponseEntity<?> acceptLoan(@RequestHeader("X-User-ID") String adminId, @PathVariable Long loanId) {
        if (adminId == null || adminId.isEmpty() || loanId == null) {
            return ResponseEntity.badRequest().build();
        }

        LoanDto acceptedLoan = service.acceptLoan(loanId, adminId);
        return ResponseEntity.ok(acceptedLoan);

    }

    @Operation(summary = "Reject a pending loan", description = "Rejects a pending loan application by admin")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Loan rejected successfully"), @ApiResponse(responseCode = "400", description = "Invalid input or loan cannot be rejected"), @ApiResponse(responseCode = "404", description = "Loan not found"), @ApiResponse(responseCode = "403", description = "Admin validation failed")})
    @PutMapping("/{loanId}/reject")
    public ResponseEntity<?> rejectLoan(@RequestHeader("X-User-ID") String adminId, @PathVariable Long loanId) {
        if (adminId == null || adminId.isEmpty() || loanId == null) {
            return ResponseEntity.badRequest().build();
        }

        LoanDto rejectedLoan = service.rejectLoan(loanId, adminId);
        return ResponseEntity.ok(rejectedLoan);
    }


    @Operation(summary = "Accept proposed loan changes", description = "User accepts the proposed changes on their loan")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Loan changes accepted successfully"), @ApiResponse(responseCode = "400", description = "Invalid input or loan cannot accept changes"), @ApiResponse(responseCode = "404", description = "Loan not found")})
    @PutMapping("/{loanId}/accept-changes")
    public ResponseEntity<?> acceptProposedChanges(@RequestHeader("X-User-ID") String userId, @PathVariable Long loanId) {
        if (userId == null || userId.isEmpty() || loanId == null || loanId <= 0 || userId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        LoanDto acceptedLoan = service.acceptProposedChanges(loanId, userId);
        return ResponseEntity.ok(acceptedLoan);

    }

    @Operation(summary = "Reject proposed loan changes", description = "User rejects the proposed changes on their loan")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Loan changes rejected successfully"), @ApiResponse(responseCode = "400", description = "Invalid input or loan cannot reject changes"), @ApiResponse(responseCode = "404", description = "Loan not found")})
    @PutMapping("/{loanId}/reject-changes")
    public ResponseEntity<?> rejectProposedChanges(@RequestHeader("X-User-ID") String userId, @PathVariable Long loanId) {
        if (userId == null || userId.isEmpty() || loanId == null || loanId <= 0 || userId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        LoanDto rejectedLoan = service.rejectProposedChanges(loanId, userId);
        return ResponseEntity.ok(rejectedLoan);

    }

    @Operation(summary = "Get top active loans for user", description = "Retrieve the top 4 active loans for a specific user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Top active loans retrieved successfully"), @ApiResponse(responseCode = "204", description = "No active loans found for the user", content = @Content), @ApiResponse(responseCode = "400", description = "Invalid or missing user ID", content = @Content), @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @GetMapping("/get/top-active-loans")
    public ResponseEntity<List<LoanDto>> getTopActiveLoans(@RequestHeader("X-User-ID") String userId) {
        if (userId == null || userId.isBlank()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        List<LoanDto> topActiveLoans = service.getTopActiveLoans(userId);
        if (topActiveLoans == null || topActiveLoans.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(topActiveLoans);
    }

    @PutMapping("/evict/user-loans-cache")
    public ResponseEntity<Void> evictUserLoansCache(@RequestHeader("X-User-ID") String adminId, @RequestParam String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        service.crossServiceEvictUserLoansCache(adminId, userId);
        return ResponseEntity.ok().build();
    }

}