package com.ylli.accounts_service.repositories;

import com.ylli.shared.models.Account;
import com.ylli.shared.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountsRepository extends JpaRepository<Account, String> {
    List<Account> findByUser(User userId);

}
