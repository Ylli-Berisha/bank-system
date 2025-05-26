package com.ylli.shared.dtos;

import com.ylli.shared.base.IdentifiableDto;
import com.ylli.shared.enums.AuditType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "Audit Data Transfer Object",
        title = "Audit DTO"
)
public class AuditDto implements IdentifiableDto<Long> {

    @Schema(
            description = "Unique identifier for the audit record (Pass as null or blank, id is generated server-side)",
            example = ""
    )
    private Long id;

    @NotNull(message = "Account cannot be null")
    @Schema(
            description = "Account associated with the audit record",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String accountId;

    @NotNull(message = "Audit type cannot be null")
    @Schema(
            description = "Type of the audit (e.g., transaction, account update), value is enumerated",
            example = "transaction"
    )
    private AuditType type;

    @Size(max = 255, message = "Details must not exceed 255 characters")
    @Schema(
            description = "Details of the audit record",
            example = "Transaction of $100.00"
    )
    private String details;

    @NotNull(message = "Created date cannot be null")
    @PastOrPresent(message = "Created date must be in the past or present")
    @Schema(
            description = "Date and time when the audit record was created, value is generated server-side, pass as null or empty",
            example = ""
    )
    private LocalDateTime createdAt;

    @NotNull(message = "Updated date cannot be null")
    @PastOrPresent(message = "Updated date must be in the past or present")
    @Schema(
            description = "Date and time when the audit record was last updated, value is generated server-side, pass as null or empty",
            example = ""
    )
    private LocalDateTime updatedAt;
}