package com.ylli.shared.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "loans")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Loan {
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
    private Double interestRate;

    //make this enum
    @Column
    private String status;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
