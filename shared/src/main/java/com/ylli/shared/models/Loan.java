package com.ylli.shared.models;

import com.ylli.shared.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity(name = "loans")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Loan extends BaseEntity<Long> {
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.id = id;
    }

}
