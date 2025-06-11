package com.ylli.transactions_service.repositories;

import com.ylli.shared.enums.TransactionStatus;
import com.ylli.shared.enums.TransactionType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> { // IMPORTANT: Add JpaSpecificationExecutor!
    List<Transaction> findByAccount(Account account);

//    @Query("SELECT t FROM transactions t JOIN t.account a JOIN a.user u WHERE " +
//            "u.id = :userId AND " +
//            "(:type IS NULL OR t.type = :type) AND " +
//            "(:status IS NULL OR t.status = :status) AND " +
//            "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
//            "(:endDate IS NULL OR t.createdAt <= :endDate) AND " +
//            "t.amount >= :minAmount AND " +
//            "t.amount <= :maxAmount AND " +
//            "(:query IS NULL OR LOWER(t.details) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.id) LIKE LOWER(CONCAT('%', :query, '%')))")
//    List<Transaction> findUserTransactionsWithFilters(
//            @Param("userId") String userId,
//            @Param("type") TransactionType type,
//            @Param("status") TransactionStatus status,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate,
//            @Param("minAmount") BigDecimal minAmount,
//            @Param("maxAmount") BigDecimal maxAmount,
//            @Param("query") String query
//    );
}
