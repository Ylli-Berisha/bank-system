package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.transactions_service.services.TransactionsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Transactions",
        description = "Operations related to transactions"
)
@RestController
@RequestMapping("/api/transactions/")
public class TransactionsController extends BaseController<TransactionDto, String, TransactionsService> {

    public TransactionsController(TransactionsService service) {
        super(service);
    }


}
