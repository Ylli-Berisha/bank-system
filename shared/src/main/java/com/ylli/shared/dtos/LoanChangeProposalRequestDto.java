package com.ylli.shared.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin proposal for changes to a pending loan")
public class LoanChangeProposalRequestDto {

    @Schema(description = "Proposed new loan amount", example = "8000.00")
    private BigDecimal proposedAmount;

    @Schema(description = "Proposed new interest rate", example = "4.5")
    private Double proposedInterestRate;

    @Schema(description = "Proposed new loan term (months)", example = "48")
    private Integer proposedTermInMonths;
}
