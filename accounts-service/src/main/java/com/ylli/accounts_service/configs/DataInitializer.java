package com.ylli.accounts_service.configs;

import com.ylli.accounts_service.repositories.AccountsRepository;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AccountType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner  {
    private final AccountsRepository accountsRepository;
    private final UsersFeignClient usersFeignClient;

    public DataInitializer(AccountsRepository accountsRepository, UsersFeignClient usersFeignClient) {
        this.accountsRepository = accountsRepository;
        this.usersFeignClient = usersFeignClient;
    }

    @Override
    public void run(String... args) throws Exception {
        UserDto userDto = usersFeignClient.getDefaultUser().getBody();

        if (accountsRepository.count() == 0) {
            User user = new User();
            user.setId(userDto.getId());
            Account account = new Account();
            account.setUser(user);
            account.setType(AccountType.SAVINGS);
            account.setBalance(BigDecimal.ZERO);
            account.setStatus(AccountStatus.ACTIVE);
            accountsRepository.save(account);
        }
    }
}
