package com.ylli.accounts_service.repositories;

import com.ylli.shared.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Account, String> {
}
