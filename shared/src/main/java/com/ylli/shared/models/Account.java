package com.ylli.shared.models;

    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.Id;
    import jakarta.persistence.ManyToOne;

    import jakarta.validation.constraints.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    @Entity(name = "accounts")
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public class Account {

        @Id
        private String id;

        @ManyToOne
        private User user;

        @Column
        private String type;

        @Column
        @NotNull(message = "Balance cannot be null")
        @Digits(integer = 13, fraction = 2, message = "Balance must have up to 13 digits and 2 decimal places")
        private BigDecimal balance;

        @Column
        private String status;

        @Column
        @NotNull(message = "Created date cannot be null")
        private LocalDateTime createdAt;

        @Column
        @NotNull(message = "Updated date cannot be null")
        private LocalDateTime updatedAt;
    }