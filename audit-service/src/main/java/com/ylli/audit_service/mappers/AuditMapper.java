package com.ylli.audit_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.models.Audit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditMapper extends BaseMapper<Audit, AuditDto> {
}
