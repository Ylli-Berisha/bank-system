package com.ylli.shared.models;

import com.ylli.shared.base.BaseEntity;
import com.ylli.shared.enums.AuditType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "audits")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Audit extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "Account cannot be null")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @NotNull(message = "Audit type cannot be null")
    private AuditType type;

    @Column(length = 255)
    @Size(max = 255, message = "Details must not exceed 255 characters")
    private String details;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}