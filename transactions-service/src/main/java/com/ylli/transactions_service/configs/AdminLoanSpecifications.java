package com.ylli.transactions_service.configs;

import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.enums.LoanType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminLoanSpecifications {

    public static Specification<Loan> withFilters(
            String userId,
            String username,
            String email,
            LoanType loanType,
            LoanStatus status,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount
    ) {
        return (root, queryObj, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Loan, Account> accountJoin = root.join("account");
            Join<Account, User> userJoin = accountJoin.join("user");

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

            if (loanType != null) {
                predicates.add(criteriaBuilder.equal(root.get("loanType"), loanType));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate));
            }

            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
