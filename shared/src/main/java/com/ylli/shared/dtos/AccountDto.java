package com.ylli.shared.dtos;

    import com.ylli.shared.models.User;
    import jakarta.validation.constraints.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AccountDto {

        @NotBlank(message = "Account ID cannot be blank")
        @Size(max = 255, message = "Account ID must not exceed 255 characters")
        private String id;

        @NotNull(message = "User cannot be null")
        private User user;

        @NotBlank(message = "Account type cannot be blank")
        @Size(max = 50, message = "Account type must not exceed 50 characters")
        private String type;

        @NotNull(message = "Balance cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be zero or positive")
        @Digits(integer = 13, fraction = 2, message = "Balance must have up to 13 digits and 2 decimal places")
        private BigDecimal balance;

        @NotBlank(message = "Status cannot be blank")
        @Size(max = 50, message = "Status must not exceed 50 characters")
        private String status;

        @NotNull(message = "Created date cannot be null")
        @PastOrPresent(message = "Created date must be in the past or present")
        private LocalDateTime createdAt;

        @NotNull(message = "Updated date cannot be null")
        @PastOrPresent(message = "Updated date must be in the past or present")
        private LocalDateTime updatedAt;
    }