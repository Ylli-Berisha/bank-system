package com.ylli.audit_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Audit;
import com.ylli.shared.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditMapper extends BaseMapper<Audit, AuditDto> {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "userId", source = "user.id")
    AuditDto toDto(Audit audit);

    default Audit toEntity(AuditDto auditDto) {
        if (auditDto == null) {
            return null;
        }

        Audit audit = new Audit();

        audit.setId(auditDto.getId());
        audit.setType(auditDto.getType());
        audit.setDetails(auditDto.getDetails());
        audit.setCreatedAt(auditDto.getCreatedAt());
        audit.setUpdatedAt(auditDto.getUpdatedAt());

        if (auditDto.getAccountId() != null) {
            Account account = new Account();
            account.setId(auditDto.getAccountId());
            audit.setAccount(account);
        } else {
            audit.setAccount(null);
        }

        if (auditDto.getUserId() != null) {
            User user = new User();
            user.setId(auditDto.getUserId());
            audit.setUser(user);
        } else {
            audit.setUser(null);
        }

        return audit;
    }
}
