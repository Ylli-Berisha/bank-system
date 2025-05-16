package com.ylli.audit_service.controllers;

import com.ylli.audit_service.services.AuditService;
import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.AuditDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit/")
public class AuditController extends BaseController<AuditDto, Long, AuditService> {
    public AuditController(AuditService service) {
        super(service);
    }
}
