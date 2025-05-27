package com.ylli.transactions_service.controllers;

import com.ylli.shared.base.BaseFallbackController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionsFallbackController extends BaseFallbackController {
}
