package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.transactions_service.services.LoansService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
