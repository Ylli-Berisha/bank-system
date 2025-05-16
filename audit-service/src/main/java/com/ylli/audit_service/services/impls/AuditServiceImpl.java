package com.ylli.audit_service.services.impls;

import com.ylli.audit_service.mappers.AuditMapper;
import com.ylli.audit_service.repositories.AuditRepository;
import com.ylli.audit_service.services.AuditService;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.models.Audit;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl extends BaseServiceImpl<Audit, AuditDto, Long, AuditRepository, AuditMapper> implements AuditService {
    public AuditServiceImpl(AuditRepository auditRepository, AuditMapper auditMapper) {
        super(auditRepository, auditMapper);
    }
}
