package com.ylli.audit_service.controllers;

import com.ylli.audit_service.services.AuditService;
import com.ylli.shared.base.BaseController;
import com.ylli.shared.dtos.AuditDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Filter audits", description = "Filter audit logs for the admin panel based on optional criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audits filtered successfully"),
            @ApiResponse(responseCode = "204", description = "No matching audits found", content = @Content)
    })
    @GetMapping("/filter/admin-audits")
    public ResponseEntity<Page<AuditDto>> filterAdminAudits(
            @RequestHeader("X-User-ID") String adminId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (adminId == null || adminId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Page<AuditDto> audits = service.filterAudits(
                adminId, userId, accountId, type, startDate, endDate, query, page, size
        );

        if (audits == null || audits.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(audits);
    }

}
