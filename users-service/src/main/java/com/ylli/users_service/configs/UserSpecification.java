package com.ylli.users_service.configs;

import com.ylli.shared.dtos.UserFilterDto;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(UserFilterDto filter) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getId() != null) {
                predicates.add(cb.equal(root.get("id"), filter.getId()));
            }

            if (filter.getUsername() != null) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + filter.getUsername().toLowerCase() + "%"));
            }

            if (filter.getFirstName() != null) {
                predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + filter.getFirstName().toLowerCase() + "%"));
            }

            if (filter.getLastName() != null) {
                predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + filter.getLastName().toLowerCase() + "%"));
            }

            if (filter.getEmail() != null) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
            }

            if (filter.getPhoneNumber() != null) {
                predicates.add(cb.like(root.get("phoneNumber"), "%" + filter.getPhoneNumber() + "%"));
            }

            if (filter.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
            }

            // Declare accountJoin only if needed:
            Join<User, Account> accountJoin = null;
            boolean needAccountJoin = filter.getAccountId() != null
                    || filter.getLoanId() != null
                    || filter.getTransactionId() != null;

            if (needAccountJoin) {
                accountJoin = root.join("accounts", JoinType.LEFT);
            }

            if (filter.getAccountId() != null) {
                predicates.add(cb.equal(accountJoin.get("id"), filter.getAccountId()));
            }

            if (filter.getLoanId() != null) {
                Join<Account, Loan> loanJoin = accountJoin.join("loans", JoinType.LEFT);
                predicates.add(cb.equal(loanJoin.get("id"), filter.getLoanId()));
            }

            if (filter.getTransactionId() != null) {
                Join<Account, Transaction> transactionJoin = accountJoin.join("transactions", JoinType.LEFT);
                predicates.add(cb.equal(transactionJoin.get("id"), filter.getTransactionId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
