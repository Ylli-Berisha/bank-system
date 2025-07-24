package com.ylli.transactions_service.repositories;

import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoansRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    List<Loan> findByAccount(Account account);

    List<Loan> findByAccountAndStatus(Account account, LoanStatus status);

    List<Loan> findByStatus(LoanStatus status);

    @Query("""
        SELECT l FROM loans l
        JOIN l.account a
        WHERE a.user.id = :userId
          AND l.status = 'ACTIVE'
        ORDER BY l.startDate DESC
    """)
    List<Loan> findTop4ActiveLoansByUserId(@Param("userId") String userId, Pageable pageable);


}
