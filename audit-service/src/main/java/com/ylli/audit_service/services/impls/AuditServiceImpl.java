package com.ylli.audit_service.services.impls;

import com.ylli.audit_service.config.AuditSpecifications;
import com.ylli.audit_service.mappers.AuditMapper;
import com.ylli.audit_service.repositories.AuditRepository;
import com.ylli.audit_service.services.AuditService;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.enums.AuditType;
import com.ylli.shared.enums.UserRole;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import com.ylli.shared.models.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuditServiceImpl extends BaseServiceImpl<Audit, AuditDto, Long, AuditRepository, AuditMapper> implements AuditService {
    private final UsersFeignClient usersFeignClient;

    public AuditServiceImpl(AuditRepository auditRepository, AuditMapper auditMapper, UsersFeignClient usersFeignClient) {
        super(auditRepository, auditMapper);
        this.usersFeignClient = usersFeignClient;
    }

    @Override
    public Page<AuditDto> filterAudits(
            String adminId,
            String userId,
            String accountId,
            String typeString,
            String startDate,
            String endDate,
            String query,
            int page,
            int size
    ) {
        validateAdmin(adminId);

        LocalDateTime parsedStartDateTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            parsedStartDateTime = LocalDate.parse(startDate).atStartOfDay();
        }

        LocalDateTime parsedEndDateTime = null;
        if (endDate != null && !endDate.isEmpty()) {
            parsedEndDateTime = LocalDate.parse(endDate).atTime(23, 59, 59, 999999999);
        }

        AuditType parsedType = null;
        if (typeString != null && !typeString.isEmpty()) {
            try {
                parsedType = AuditType.valueOf(typeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid AuditType string: " + typeString);
                throw e;
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Audit> auditPage = repository.findAll(
                AuditSpecifications.filterAudits(
                        userId,
                        accountId,
                        parsedType,
                        query,
                        parsedStartDateTime,
                        parsedEndDateTime
                ),
                pageable
        );

        return auditPage.map(mapper::toDto);
    }


    public void validateAdmin(String adminId) {
        if (adminId == null || adminId.isEmpty()) {
            throw new IllegalArgumentException("Admin ID cannot be null or empty");
        }
        var user = usersFeignClient.getUser(adminId).getBody();

        if (user == null) {
            throw new ResourceNotFoundException("User with ID " + adminId + " is not an admin");
        }
        if (!user.getRoles().contains(UserRole.ROLE_ADMIN))
            throw new IllegalArgumentException("User with ID " + adminId + " is not an admin");
    }

}
