package com.ylli.transactions_service.configs;

import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.enums.LoanType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanSpecifications {

    public static Specification<Loan> withFilters(
            String userId,
            LoanType loanType,
            LoanStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Double minAmount,
            Double maxAmount,
            String query) {

        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Loan, Account> accountJoin = root.join("account");
            Join<Account, User> userJoin = accountJoin.join("user");
            predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));

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

            if (maxAmount != null && maxAmount < Double.MAX_VALUE) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            if (query != null && !query.isEmpty()) {
                String lowerCaseQuery = "%" + query.toLowerCase() + "%";

                Predicate idLike = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("id").as(String.class)),
                        lowerCaseQuery);

                Predicate loanTypeLike = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("loanType").as(String.class)),
                        lowerCaseQuery);

                predicates.add(criteriaBuilder.or(idLike, loanTypeLike));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}