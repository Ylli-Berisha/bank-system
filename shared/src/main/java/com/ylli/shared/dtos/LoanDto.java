package com.ylli.shared.dtos;

import com.ylli.shared.enums.LoanStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {

    private Long id;

    @NotNull(message = "Account cannot be null")
    private AccountDto account;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Interest rate cannot be null")
    @PositiveOrZero(message = "Interest rate must be zero or positive")
    @DecimalMax(value = "100.0", message = "Interest rate cannot exceed 100%")
    private Double interestRate;

    @NotNull(message = "Loan status cannot be null")
    private LoanStatus status;

    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull(message = "Created date cannot be null")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated date cannot be null")
    private LocalDateTime updatedAt;
}