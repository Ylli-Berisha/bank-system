package com.ylli.transactions_service.repositories;

import com.ylli.shared.models.Account;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccount(Account account);
}
