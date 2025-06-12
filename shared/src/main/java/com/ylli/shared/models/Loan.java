package com.ylli.shared.models;

import com.ylli.shared.base.BaseEntity;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.enums.LoanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name = "loans")
@Table(indexes = {
        @Index(name = "idx_loan_account", columnList = "account_id"),
        @Index(name = "idx_loan_status", columnList = "status")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Loan extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "Account cannot be null")
    private Account account;

    @Column(nullable = false)
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @Column(nullable = false)
    @NotNull(message = "Interest rate cannot be null")
    @PositiveOrZero(message = "Interest rate must be zero or positive")
    private Double interestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Loan status cannot be null")
    private LoanStatus status;

    @NotNull(message = "Loan type cannot be null")
    @Schema(
            description = "Type of the loan (e.g., PERSONAL_LOAN, AUTO_LOAN), value is enumerated",
            example = "PERSONAL_LOAN"
    )
    private LoanType loanType;

    @Column
//    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDate startDate;

    @Column
//    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @Schema(
            description = "Actual date when the loan was fully paid off (only applicable for PAID_OFF status)",
            example = "2024-03-15",
            nullable = true
    )
    private LocalDate paidOffDate;

    private int termInMonths;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}