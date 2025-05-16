package com.ylli.shared.dtos;

import com.ylli.shared.enums.AuditType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDto {

    private Long id;

    @NotNull(message = "Account cannot be null")
    private AccountDto account;

    @NotNull(message = "Audit type cannot be null")
    private AuditType type;

    @Size(max = 255, message = "Details must not exceed 255 characters")
    private String details;

    @NotNull(message = "Created date cannot be null")
    @PastOrPresent(message = "Created date must be in the past or present")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated date cannot be null")
    @PastOrPresent(message = "Updated date must be in the past or present")
    private LocalDateTime updatedAt;
}