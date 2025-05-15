package com.ylli.shared.models;

import com.ylli.shared.base.BaseEntity;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;

@Entity(name = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account extends BaseEntity<String> {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;


    @Column
    @NotNull(message = "Account type cannot be null")
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column
    @NotNull(message = "Balance cannot be null")
    @Digits(integer = 13, fraction = 2, message = "Balance must have up to 13 digits and 2 decimal places")
    private BigDecimal balance;

    @Column
    @NotNull(message = "Account status cannot be null")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

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