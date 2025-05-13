package com.ylli.shared.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Audit {

    @Id
    private Long id;

    @OneToOne
    private User user;

    //make this enum
    @Column
    private String type;

    @Column
    private String details;

    @Column
    private LocalDateTime createAt;

}
