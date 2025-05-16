package com.ylli.transactions_service.repositories;

import com.ylli.shared.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoansRepository extends JpaRepository<Loan, Long> {
}
