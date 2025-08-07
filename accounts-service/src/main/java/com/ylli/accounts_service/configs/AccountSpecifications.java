package com.ylli.accounts_service.configs;

import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AccountType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountSpecifications {

    public static Specification<Account> withFilters(
            String accountId,
            AccountType type,
            BigDecimal minBalance,
            BigDecimal maxBalance,
            AccountStatus status,
            String userId,
            String username,
            String email,
            String loanId,
            String transactionId
    ) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Account, User> userJoin = root.join("user");

            if (accountId != null && !accountId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), accountId));
            }

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            if (minBalance != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), minBalance));
            }
            if (maxBalance != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("balance"), maxBalance));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (userId != null && !userId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));
            }

            if (username != null && !username.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(userJoin.get("username")),
                        "%" + username.toLowerCase() + "%"
                ));
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(userJoin.get("email")),
                        "%" + email.toLowerCase() + "%"
                ));
            }

            if (loanId != null && !loanId.isEmpty()) {
                Join<Account, Loan> loanJoin = root.join("loans"); // Adjust if field name differs
                predicates.add(criteriaBuilder.equal(loanJoin.get("id"), loanId));
            }

            if (transactionId != null && !transactionId.isEmpty()) {
                Join<Account, Transaction> transactionJoin = root.join("transactions"); // Adjust if field name differs
                predicates.add(criteriaBuilder.equal(transactionJoin.get("id"), transactionId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
