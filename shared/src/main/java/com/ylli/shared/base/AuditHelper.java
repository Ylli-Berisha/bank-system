package com.ylli.shared.base;

import com.ylli.shared.clients.AuditFeignClient;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.enums.AuditType;
import org.springframework.stereotype.Component;

@Component
public class AuditHelper {

    private final AuditFeignClient auditFeignClient;

    public AuditHelper(AuditFeignClient auditFeignClient) {
        this.auditFeignClient = auditFeignClient;
    }

    public void createAudit(AuditType type, String details, String accountId) {
        AuditDto audit = new AuditDto();
        audit.setType(type);
        audit.setDetails(details);
        audit.setAccountId(accountId);
        auditFeignClient.createAudit(audit);
    }

    public void createAuditWithUser(AuditType type, String details, String userId) {
        AuditDto audit = new AuditDto();
        audit.setType(type);
        audit.setDetails(details);
        audit.setUserId(userId);
        auditFeignClient.createAudit(audit);
    }
}
