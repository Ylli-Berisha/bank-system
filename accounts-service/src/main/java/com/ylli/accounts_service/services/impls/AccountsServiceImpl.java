package com.ylli.accounts_service.services.impls;

import com.ylli.accounts_service.repositories.AccountsRepository;
import com.ylli.accounts_service.services.AccountsService;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.accounts_service.mappers.AccountMapper;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AccountType;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
public class AccountsServiceImpl extends BaseServiceImpl<Account, AccountDto, String, AccountsRepository, AccountMapper> implements AccountsService {

    private static final Logger log = LoggerFactory.getLogger(AccountsServiceImpl.class);
    private final UsersFeignClient usersFeignClient;

    @Autowired
    public AccountsServiceImpl(AccountsRepository repository, AccountMapper mapper, UsersFeignClient usersFeignClient) {
        super(repository, mapper);
        this.usersFeignClient = usersFeignClient;
    }

    @Override
    public List<AccountDto> getUserAccounts(String userId) {
        User user = new User();
        user.setId(userId);

        List<Account> accounts = repository.findByUser(user);
        if (accounts == null || accounts.isEmpty()) {
            return null;
        }

        return mapper.toDtoList(accounts);
    }

    @Override
    public AccountDto getDefaultAccount() {
        var userDto = usersFeignClient.getDefaultUser().getBody();
        var user = new User();

        if (userDto == null) {
            throw new IllegalArgumentException("Default user not found");
        }
        user.setId(userDto.getId());
        Account account = repository.findByUser(user).getFirst();
        return mapper.toDto(account);
    }

    @Override
    public List<String> getAccountTypes() {
        List<AccountType> types = List.of(AccountType.values());
        return types.stream()
                .map(AccountType::name)
                .toList();
    }

    @Override
    public List<String> getAccountStatuses() {
        List<AccountStatus> statuses = List.of(AccountStatus.values());
        return statuses.stream()
                .map(AccountStatus::name)
                .toList();
    }

    @Override
    public Boolean applyForNewAccount(AccountDto accountDto) {
        UserDto user = usersFeignClient.getUser(accountDto.getUserId()).getBody();
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + accountDto.getUserId());
        }

        try {
            Account account = mapper.toEntity(accountDto);
//            User userEntity = new User();
//            userEntity.setId(user.getId());
//            account.setUser(userEntity);
            repository.save(account);

            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("applyForNewAccount error", e);
            return Boolean.FALSE;
        }
    }

    @Override
    public Boolean freezeAccount(String accountId) throws RuntimeException {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            log.warn("Account with ID {} is already frozen", accountId);
            return Boolean.FALSE;
        }

        try {
            account.setStatus(AccountStatus.FROZEN);
            repository.save(account);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error freezing account with ID {}", accountId, e);
            throw new RuntimeException("Failed to freeze account", e);
        }
    }

    @Override
    public Boolean unfreezeAccount(String accountId) {
        Account account = repository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));
        if (account.getStatus() != AccountStatus.FROZEN) {
            log.warn("Account with ID {} is not frozen", accountId);
            return Boolean.FALSE;
        }
        try {
            account.setStatus(AccountStatus.ACTIVE);
            repository.save(account);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error unfreezing account with ID {}", accountId, e);
            throw new RuntimeException("Failed to unfreeze account", e);
        }
    }

    @Override
    public AccountDto getByIdAndUserId(String id, String userId) {
        User user = new User();
        user.setId(userId);

        Account account = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        if (!account.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Account does not belong to the user with ID: " + userId);
        }

        return mapper.toDto(account);
    }
}


//@Service
//public class AccountsServiceImpl implements AccountsService {
//
//    @Autowired
//    private AccountsRepository accountsRepository;
//
//    @Autowired
//    private AccountMapper accountMapper;
//
//    @Override
//    public AccountDto getById(String accountId) {
//        if (accountId == null || accountId.isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//
//        Optional<Account> accountOptional = accountsRepository.findById(accountId);
//        Account account = accountOptional.orElseThrow(() ->
//                new IllegalArgumentException("Account not found with ID: " + accountId));
//
//        return convertToDto(account);
//    }
//
//    @Override
//    public AccountDto create(AccountDto accountDto) {
//        if (accountDto == null) {
//            throw new IllegalArgumentException("Account cannot be null");
//        }
//        if (accountDto.getId() == null || accountDto.getId().isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//        if (accountDto.getBalance() == null || accountDto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
//            throw new IllegalArgumentException("Account balance cannot be null or negative");
//        }
//
//        Account account = convertToEntity(accountDto);
//        Account savedAccount = accountsRepository.save(account);
//        return convertToDto(savedAccount);
//    }
//
//    @Override
//    public AccountDto update(String id, AccountDto accountDto) {
//        if (accountDto == null) {
//            throw new IllegalArgumentException("Account cannot be null");
//        }
//        if (accountDto.getId() == null || accountDto.getId().isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//        if (!accountsRepository.existsById(accountDto.getId())) {
//            throw new IllegalArgumentException("Account not found with ID: " + accountDto.getId());
//        }
//
//        Account account = convertToEntity(accountDto);
//        Account savedAccount = accountsRepository.save(account);
//        return convertToDto(savedAccount);
//    }
//
//    @Override
//    public AccountDto delete(String accountId) {
//        if (accountId == null || accountId.isEmpty()) {
//            throw new IllegalArgumentException("Account ID cannot be null or empty");
//        }
//
//        Optional<Account> accountOptional = accountsRepository.findById(accountId);
//        if (accountOptional.isEmpty()) {
//            throw new IllegalArgumentException("Account not found with ID: " + accountId);
//        }
//
//        Account account = accountOptional.get();
//        accountsRepository.deleteById(accountId);
//        return convertToDto(account);
//    }
//
//    private AccountDto convertToDto(Account account) {
//        return accountMapper.toDto(account);
//    }
//
//    private Account convertToEntity(AccountDto accountDto) {
//        return accountMapper.toEntity(accountDto);
//    }
//}