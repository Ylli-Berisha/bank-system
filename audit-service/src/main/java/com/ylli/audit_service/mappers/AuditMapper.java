package com.ylli.audit_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.models.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditMapper extends BaseMapper<Audit, AuditDto> {

    @Mapping(target = "accountId", source = "account.id")
    @Override
    AuditDto toDto(Audit audit);

    @Mapping(target = "account.id", source = "auditDto.accountId")
    @Override
    Audit toEntity(AuditDto auditDto);
}
