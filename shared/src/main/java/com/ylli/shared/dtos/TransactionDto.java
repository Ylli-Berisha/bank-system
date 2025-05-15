package com.ylli.shared.dtos;

import com.ylli.shared.enums.TransactionStatus;
import com.ylli.shared.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private String id;

    @NotBlank(message = "Account ID cannot be blank")
    private String accountId;

    @NotNull(message = "Transaction type cannot be null")
    private TransactionType type;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Transaction time cannot be null")
    private LocalDateTime createdAt;

    @NotNull(message = "Transaction status cannot be null")
    private TransactionStatus status;

    @Size(max = 255, message = "Details cannot exceed 255 characters")
    private String details;

    private String recipientAccountId;
}