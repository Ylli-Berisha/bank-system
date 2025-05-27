package com.ylli.audit_service.controllers;

import com.ylli.audit_service.services.AuditService;
import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.AuditDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Audit",
        description = "Operations related to audit"
)
@RestController
@RequestMapping("/api/audit")
public class AuditController extends BaseController<AuditDto, Long, AuditService> {
    @Autowired
    public AuditController(AuditService service) {
        super(service);
    }
}
