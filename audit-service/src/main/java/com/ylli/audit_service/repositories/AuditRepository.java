package com.ylli.audit_service.repositories;

import com.ylli.shared.models.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    Page<Audit> findAll(Specification<Audit> auditSpecification, Pageable pageable);
}
