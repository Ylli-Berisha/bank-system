package com.ylli.shared.dtos; // New package or existing DTOs

import com.ylli.shared.enums.LoanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "Loan Application Request Data Transfer Object",
        title = "Loan Application Request DTO"
)
public class LoanApplicationRequestDto {

    @NotNull(message = "Loan type cannot be null")
    @Schema(
            description = "Type of the loan (e.g., PERSONAL_LOAN, AUTO_LOAN), value is enumerated",
            example = "PERSONAL_LOAN"
    )
    private LoanType loanType;

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

    @NotNull(message = "Loan term in months cannot be null")
    @Positive(message = "Loan term must be positive")
    @Schema(
            description = "Duration of the loan in months",
            example = "60"
    )
    private Integer termInMonths;
}