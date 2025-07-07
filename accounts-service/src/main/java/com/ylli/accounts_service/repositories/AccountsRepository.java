package com.ylli.accounts_service.repositories;

import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Account, String> {
    List<Account> findByUser(User userId);

    List<Account> findTop4ByUserAndStatusOrderByCreatedAtDesc(User user, AccountStatus status);

}
