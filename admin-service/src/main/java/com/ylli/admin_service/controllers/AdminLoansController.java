package com.ylli.admin_service.controllers;

import com.ylli.admin_service.services.AdminLoansService;
import com.ylli.shared.dtos.LoanDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
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
                adminId, userId, username, email, startDate, endDate, type, status, minAmount, maxAmount,
                 page, size
        );
        return ResponseEntity.ok(loans);
    }

}
