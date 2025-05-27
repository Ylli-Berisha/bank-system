package com.ylli.accounts_service.controllers;

import com.ylli.shared.base.BaseFallbackController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountsFallbackController extends BaseFallbackController {
}
