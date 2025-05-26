package com.ylli.shared.dtos;

import com.ylli.shared.base.IdentifiableDto;
import com.ylli.shared.enums.LoanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "Loan Data Transfer Object",
        title = "Loan DTO"
)
public class LoanDto implements IdentifiableDto<Long> {

    @Schema(
            description = "Unique identifier for the loan (Pass as null or blank, id is generated server-side)",
            example = ""
    )
    private Long id;

    @NotNull(message = "Account cannot be null")
    @Schema(
            description = "Account associated with the loan",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String accountId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @Schema(
            description = "Loan amount",
            example = "10000.00"
    )
    private Double amount;

    @NotNull(message = "Interest rate cannot be null")
    @PositiveOrZero(message = "Interest rate must be zero or positive")
    @DecimalMax(value = "100.0", message = "Interest rate cannot exceed 100%")
    @Schema(
            description = "Interest rate on the loan",
            example = "5.0"
    )
    private Double interestRate;

    @NotNull(message = "Loan status cannot be null")
    @Schema(
            description = "Current status of the loan (e.g., approved, pending, rejected), value is enumerated",
            example = "approved"
    )
    private LoanStatus status;

    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date must be in the past or present")
    @Schema(
            description = "Start date of the loan",
            example = "2023-01-01"
    )
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    @Schema(
            description = "End date of the loan",
            example = "2024-01-01"
    )
    private LocalDate endDate;

    @NotNull(message = "Created date cannot be null")
    @Schema(
            description = "Date and time when the loan was created, value is generated server-side, pass as null or empty",
            example = ""
    )
    private LocalDateTime createdAt;

    @NotNull(message = "Updated date cannot be null")
    @Schema(
            description = "Date and time when the loan was last updated, value is generated server-side, pass as null or empty",
            example = ""
    )
    private LocalDateTime updatedAt;
}