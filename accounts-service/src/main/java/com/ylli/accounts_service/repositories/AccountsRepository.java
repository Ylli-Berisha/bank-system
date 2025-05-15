package com.ylli.accounts_service.repositories;

import com.ylli.shared.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Account, String> {
}
