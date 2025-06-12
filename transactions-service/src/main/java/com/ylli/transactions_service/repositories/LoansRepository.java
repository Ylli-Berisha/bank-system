package com.ylli.transactions_service.repositories;

import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoansRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    public List<Loan> findByAccount(Account account);
    List<Loan> findByAccountAndStatus(Account account, LoanStatus status);

}
