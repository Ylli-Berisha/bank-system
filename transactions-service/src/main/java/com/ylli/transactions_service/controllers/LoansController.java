package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.transactions_service.services.LoansService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Tag(name = "Loans", description = "Operations related to loans")
@RestController
@RequestMapping("/api/loans")
public class LoansController extends BaseController<LoanDto, Long, LoansService> {

    public LoansController(LoansService service) {
        super(service);
    }

    @Operation(summary = "Get loans for user", description = "Retrieve all loans for the authenticated user, optionally filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid user ID", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get/user-loans")
    public ResponseEntity<List<LoanDto>> getUserLoans(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(required = false) LoanStatus status) {

        if (userId == null || userId.isBlank()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            List<LoanDto> loans = service.getUserLoans(userId, status);
            return ResponseEntity.ok(loans != null ? loans : Collections.emptyList());
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get available loan types", description = "Retrieve all loan types supported by the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan types retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No loan types found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get/loan-types")
    public ResponseEntity<List<String>> getLoanTypes() {
        try {
            List<String> loanTypes = service.getLoanTypes();
            if (loanTypes == null || loanTypes.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(loanTypes);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Apply for a loan", description = "Submit a new loan application for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan application submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or missing parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Associated account not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/apply")
    public ResponseEntity<?> applyForNewLoan(
            @RequestParam("accountId") String accountId,
            @RequestBody LoanApplicationRequestDto requestDto,
            @RequestHeader("X-User-ID") String userId
    ) {
        if (accountId == null || accountId.isBlank() || requestDto == null || userId == null || userId.isBlank()) {
            return new ResponseEntity<>("Missing required parameters.", HttpStatus.BAD_REQUEST);
        }

        try {
            service.applyForLoan(accountId, requestDto, userId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Filter user loans", description = "Filter loans for a user based on multiple optional criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans filtered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing user ID", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/filter/user-loans")
    public ResponseEntity<List<LoanDto>> getFilteredLoans(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(name = "loanType", required = false) String loanType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "minAmount", required = false) Double minAmount,
            @RequestParam(name = "maxAmount", required = false) Double maxAmount,
            @RequestParam(name = "query", required = false) String query
    ) {
        List<LoanDto> filteredLoans = service.filterUserLoans(
                userId,
                loanType,
                status,
                startDate,
                endDate,
                minAmount,
                maxAmount,
                query
        );
        return ResponseEntity.ok(filteredLoans);
    }

    @Operation(summary = "Filter admin loans", description = "Filter loans for admin panel based on optional criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans filtered successfully"),
            @ApiResponse(responseCode = "204", description = "No matching loans found", content = @Content)
    })
    @GetMapping("/filter/admin-loans")
    public ResponseEntity<Page<LoanDto>> filterAdminLoans(
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
    ) {
        if (adminId == null || adminId.isEmpty() || page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Page<LoanDto> loans = service.filterAdminLoans(
                adminId, userId, username, email, type, status, startDate, endDate, minAmount, maxAmount, page, size
        );

        if (loans == null || loans.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(loans);
    }
}
