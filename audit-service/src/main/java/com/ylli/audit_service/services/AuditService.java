package com.ylli.audit_service.services;

import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.AuditDto;
import org.springframework.data.domain.Page;

public interface AuditService extends BaseService<AuditDto, Long> {

    Page<AuditDto> filterAudits(
            String adminId,
            String userId,
            String accountId,
            String typeString,
            String startDate,
            String endDate,
            String query,
            int page,
            int size
    );
}
