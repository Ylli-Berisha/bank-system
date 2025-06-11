package com.ylli.transactions_service.configs;

import com.ylli.shared.enums.TransactionStatus;
import com.ylli.shared.enums.TransactionType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecifications {

    public static Specification<Transaction> withFilters(
            String userId,
            TransactionType type,
            TransactionStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String query) {

        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Transaction, Account> accountJoin = root.join("account");
            Join<Account, User> userJoin = accountJoin.join("user");
            predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            if (maxAmount != null && maxAmount.compareTo(new BigDecimal("999999999999999.99")) < 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            if (query != null && !query.isEmpty()) {
                String lowerCaseQuery = query.toLowerCase();
                Predicate detailsLike = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("details")),
                        "%" + lowerCaseQuery + "%");
                Predicate idLike = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("id")),
                        "%" + lowerCaseQuery + "%");
                predicates.add(criteriaBuilder.or(detailsLike, idLike));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}