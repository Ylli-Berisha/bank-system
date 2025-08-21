package com.ylli.admin_service.controllers;

import com.ylli.admin_service.services.AdminLoansService;
import com.ylli.shared.dtos.LoanChangeProposalRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Admin Loans", description = "Administrative operations for managing loans")
@RestController
@RequestMapping("/api/loans")
public class AdminLoansController {

    private final AdminLoansService adminLoansService;

    public AdminLoansController(AdminLoansService adminLoansService) {
        this.adminLoansService = adminLoansService;
    }

    @Operation(summary = "Get filtered loans", description = "Retrieve loans based on various filter criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "loans retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "404", description = "No loans found matching the criteria"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving loans")
    })
    @GetMapping("/filter/loans")
    public ResponseEntity<Page<LoanDto>> getFilteredLoans(
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<LoanDto> loans = adminLoansService.getFilteredLoans(
                adminId, userId, username, email, type, status, startDate, endDate, minAmount, maxAmount,
                page, size
        );
        return ResponseEntity.ok(loans);
    }

    @Operation(summary = "Accept a pending loan", description = "Accepts a pending loan application by admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan accepted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or loan cannot be accepted"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "403", description = "Admin validation failed")
    })
    @PutMapping("/{loanId}/accept")
    public ResponseEntity<LoanDto> acceptLoan(
            @RequestHeader("X-User-ID") String adminId,
            @PathVariable Long loanId
    ) {
        if (adminId == null || adminId.isEmpty() || loanId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            LoanDto acceptedLoan = adminLoansService.acceptLoan(loanId, adminId);
            return ResponseEntity.ok(acceptedLoan);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "Reject a pending loan", description = "Rejects a pending loan application by admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or loan cannot be rejected"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "403", description = "Admin validation failed")
    })
    @PutMapping("/{loanId}/reject")
    public ResponseEntity<LoanDto> rejectLoan(
            @RequestHeader("X-User-ID") String adminId,
            @PathVariable Long loanId
    ) {
        if (adminId == null || adminId.isEmpty() || loanId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            LoanDto rejectedLoan = adminLoansService.rejectLoan(loanId, adminId);
            return ResponseEntity.ok(rejectedLoan);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Propose changes for a loan", description = "Proposes changes to a loan by admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Changes proposed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or loan cannot be modified"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "403", description = "Admin validation failed")
    })
    @PutMapping("/{loanId}/propose-changes")
    public ResponseEntity<LoanDto> proposeChangesForLoan(
            @RequestHeader("X-User-ID") String adminId,
            @PathVariable Long loanId,
            @RequestBody LoanChangeProposalRequestDto proposalDto
    ) {
        if (adminId == null || adminId.isEmpty() || loanId == null || proposalDto == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            LoanDto loan = adminLoansService.proposeChangesForLoan(adminId, loanId, proposalDto);
            return ResponseEntity.ok(loan);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
