package com.ylli.audit_service.config;

import com.ylli.shared.models.Audit;
import com.ylli.shared.enums.AuditType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditSpecifications {

    public static Specification<Audit> filterAudits(
            String userId,
            String accountId,
            AuditType type,
            String query,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if (accountId != null) {
                predicates.add(cb.equal(root.get("account").get("id"), accountId));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (query != null && !query.isEmpty()) {
                Predicate detailsLike = cb.like(cb.lower(root.get("details")), "%" + query.toLowerCase() + "%");

                Predicate idEqual = cb.disjunction(); // Safe fallback
                try {
                    Long idValue = Long.parseLong(query);
                    idEqual = cb.equal(root.get("id"), idValue);
                } catch (NumberFormatException ignored) {}

                predicates.add(cb.or(detailsLike, idEqual));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
