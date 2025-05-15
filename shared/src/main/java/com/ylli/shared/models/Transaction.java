package com.ylli.shared.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    private Long id;

    @ManyToOne
    private Account account;

    //make this enum
    @Column
    private String type;

    @Column
    private Double amount;

    @Column
    private LocalDateTime time;

    //make this enum
    @Column
    private String status;

    @Column
    private String details;


    @OneToOne
    private Account recipientAccount;

}
