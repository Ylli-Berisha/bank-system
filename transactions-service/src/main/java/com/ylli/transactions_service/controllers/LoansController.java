package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.transactions_service.services.LoansService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<LoanDto>> getUserLoans(@RequestParam String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        List<LoanDto> loans = service.getUserLoans(userId);
        if (loans == null || loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(loans);
        }
    }
}
