package com.ylli.shared.dtos;

import com.ylli.shared.base.IdentifiableDto;
import com.ylli.shared.enums.TransactionStatus;
import com.ylli.shared.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        description = "Transaction Data Transfer Object",
        title = "Transaction DTO"
)
public class TransactionDto implements IdentifiableDto<String> {

    @Schema(
            description = "Unique identifier for the transaction (Pass as null or blank, id is generated server-side)",
            example = ""
    )
    private String id;

    @NotBlank(message = "Account ID cannot be blank")
    @Schema(
            description = "Account associated with the transaction",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String accountId;

    @NotNull(message = "Transaction type cannot be null")
    @Schema(
            description = "Type of the transaction (e.g., deposit, withdrawal), value is enumerated",
            example = "deposit"
    )
    private TransactionType type;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    @Schema(
            description = "Amount involved in the transaction",
            example = "100.00"
    )
    private BigDecimal amount;

    @NotNull(message = "Transaction time cannot be null")
    @Schema(
            description = "Date and time when the transaction occurred, value is generated server-side, pass as null or empty",
            example = ""
    )
    private LocalDateTime createdAt;

    @NotNull(message = "Transaction status cannot be null")
    @Schema(
            description = "Current status of the transaction (e.g., completed, pending, failed), value is enumerated",
            example = "completed"
    )
    private TransactionStatus status;

    @Size(max = 255, message = "Details cannot exceed 255 characters")
    @Schema(
            description = "Details of the transaction",
            example = "Deposit of $100.00"
    )
    private String details;

    @NotBlank(message = "Recipient account ID cannot be blank")
    @Size(max = 255, message = "Recipient account ID must not exceed 255 characters")
    @Schema(
            description = "Account ID of the recipient (for transfer transactions)",
            example = "123e4567-e89b-12d3-a456-426614174001"
    )
    private String recipientAccountId;
}