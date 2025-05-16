package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.transactions_service.services.LoansService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans/")
public class LoansController extends BaseController<LoanDto, Long, LoansService> {
    public LoansController(LoansService service) {
        super(service);
    }
}
