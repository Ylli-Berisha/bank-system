package com.ylli.shared.models;

import com.ylli.shared.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "audits")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Audit extends BaseEntity<Long> {

    @Id
    private Long id;

    @OneToOne
    private User user;

    //make this enum
    @Column
    private String type;

    @Column
    private String details;

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
