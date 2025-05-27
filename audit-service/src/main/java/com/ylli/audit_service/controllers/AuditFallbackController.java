package com.ylli.audit_service.controllers;

import com.ylli.shared.base.BaseFallbackController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
public class AuditFallbackController extends BaseFallbackController {
}
