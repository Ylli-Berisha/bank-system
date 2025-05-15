package com.ylli.shared.models;

import com.ylli.shared.base.BaseEntity;
import com.ylli.shared.enums.TransactionStatus;
import com.ylli.shared.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;


@Entity(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction extends BaseEntity<String> {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @NotNull(message = "Account cannot be null")
    private Account account;

    @Column(nullable = false)
    @NotNull(message = "Transaction type cannot be null")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;


    @Column(nullable = false)
    @NotNull(message = "Transaction status cannot be null")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column
    @Size(max = 255, message = "Details cannot exceed 255 characters")
    private String details;

    @OneToOne
    @JoinColumn(name = "recipient_account_id")
    private Account recipientAccount;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        this.id = id;
    }

}
