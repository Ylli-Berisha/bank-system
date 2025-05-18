package com.ylli.shared.dtos;

    import com.ylli.shared.enums.AccountStatus;
    import com.ylli.shared.enums.AccountType;
    import com.ylli.shared.models.User;
    import io.swagger.v3.oas.annotations.media.Schema;
    import jakarta.validation.constraints.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(
            description = "Account Data Transfer Object",
            title = "Account DTO"
    )
    public class AccountDto {

        @NotBlank(message = "Account ID cannot be blank")
        @Size(max = 255, message = "Account ID must not exceed 255 characters")
        @Schema(
                description = "Unique identifier for the account (Pass as null or blank, id is generated server-side)",
                example = ""
        )
        private String id;

        @Schema(
                description = "User associated with the account",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @NotNull(message = "User cannot be null")
        private String userId;

        @NotBlank(message = "Account type cannot be blank")
        @Size(max = 50, message = "Account type must not exceed 50 characters")
        @Schema(
                description = "Type of the account (e.g., savings, checking), value is enumerated",
                example = "savings"
        )
        private AccountType type;

        @NotNull(message = "Balance cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be zero or positive")
        @Digits(integer = 13, fraction = 2, message = "Balance must have up to 13 digits and 2 decimal places")
        @Schema(
                description = "Current balance of the account",
                example = "1000.00"
        )
        private BigDecimal balance;

        @NotBlank(message = "Status cannot be blank")
        @Size(max = 50, message = "Status must not exceed 50 characters")
        @Schema(
                description = "Current status of the account (e.g., active, inactive), value is enumerated, value is enumerated",
                example = "active"
        )
        private AccountStatus status;

        @NotNull(message = "Created date cannot be null")
        @PastOrPresent(message = "Created date must be in the past or present")
        @Schema(
                description = "Date and time when the account was created, value is generated server-side, pass as null or empty",
                example = ""
        )
        private LocalDateTime createdAt;

        @NotNull(message = "Updated date cannot be null")
        @PastOrPresent(message = "Updated date must be in the past or present")
        @Schema(
                description = "Date and time when the account was last updated, value is generated server-side, pass as null or empty",
                example = ""
        )
        private LocalDateTime updatedAt;
    }