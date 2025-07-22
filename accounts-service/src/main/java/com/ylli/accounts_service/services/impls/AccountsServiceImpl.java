package com.ylli.accounts_service.services.impls;

import com.ylli.accounts_service.configs.AdminAccountSpecifications;
import com.ylli.accounts_service.repositories.AccountsRepository;
import com.ylli.accounts_service.services.AccountsService;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.accounts_service.mappers.AccountMapper;
import com.ylli.shared.dtos.UserDto;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AccountType;
import com.ylli.shared.enums.UserRole;
import com.ylli.shared.exceptions.AccountLockedException;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.User;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<AccountDto> getAll() {
        return mapper.toDtoList(repository.findAll());
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
            throw new ResourceNotFoundException("Default user not found");
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
            throw new ResourceNotFoundException("User not found with ID: " + accountDto.getUserId());
        }

        try {
            Account account = mapper.toEntity(accountDto);
//            User userEntity = new User();
//            userEntity.setId(user.getId());
//            account.setUser(userEntity);
            repository.save(account);

            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Apply for new account error", e);
            return Boolean.FALSE;
        }
    }

    @Override
    public Boolean freezeAccount(String accountId, String userId) throws RuntimeException {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + accountId));

        if (!account.getUser().getId().equals(userId)) {
            log.warn("User with ID {} does not own account with ID {}", userId, accountId);
            throw new IllegalArgumentException("User does not own the account with ID: " + accountId);
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            log.warn("Account with ID {} is already frozen", accountId);
            throw new IllegalStateException("Account with ID: " + accountId + " is already frozen or not active");
        }

        try {
            account.setStatus(AccountStatus.FROZEN);
            repository.save(account);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error freezing account with ID {}", accountId, e);
            throw e;
        }
    }

    @Override
    public Boolean unfreezeAccount(String accountId, String userId) {
        Account account = repository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
        if (account.getStatus() != AccountStatus.FROZEN) {
            log.warn("Account with ID {} is not frozen", accountId);
            throw new IllegalStateException("Account with id: " + accountId + " is not frozen");
        }
        if (!account.getUser().getId().equals(userId)) {
            log.warn("User with ID {} does not own account with ID {}", userId, accountId);
            throw new IllegalArgumentException("User does not own the account with ID: " + accountId);
        }

        try {
            account.setStatus(AccountStatus.ACTIVE);
            repository.save(account);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error unfreezing account with ID {}", accountId, e);
            throw e;
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

    @Override
    public List<AccountDto> getTopAccounts(String userId) {
        User user = new User();
        user.setId(userId);

        List<Account> accounts = repository.findTop4ByUserAndStatusOrderByCreatedAtDesc(user, AccountStatus.ACTIVE);
        if (accounts == null || accounts.isEmpty()) {
            return List.of();
        }

        return mapper.toDtoList(accounts);
    }

    @Override
    public Page<AccountDto> filterAdminAccounts(
            String adminId,
            String accountId,
            String typeString,
            BigDecimal minBalance,
            BigDecimal maxBalance,
            String statusString,
            String userId,
            String username,
            String email,
            String loanId,
            String transactionId,
            int page,
            int size
    ) {
        validateAdmin(adminId);

        AccountType parsedType = null;
        if (typeString != null && !typeString.isEmpty()) {
            try {
                parsedType = AccountType.valueOf(typeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid AccountType string: " + typeString);
                throw e;
            }
        }

        AccountStatus parsedStatus = null;
        if (statusString != null && !statusString.isEmpty()) {
            try {
                parsedStatus = AccountStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid AccountStatus string: " + statusString);
                throw e;
            }
        }

        BigDecimal actualMinBalance = (minBalance != null) ? minBalance : BigDecimal.ZERO;
        BigDecimal actualMaxBalance = (maxBalance != null) ? maxBalance : new BigDecimal("999999999999999.99");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Account> accountsPage = repository.findAll(
                AdminAccountSpecifications.withFilters(
                        accountId,
                        parsedType,
                        actualMinBalance,
                        actualMaxBalance,
                        parsedStatus,
                        userId,
                        username,
                        email,
                        loanId,
                        transactionId
                ),
                pageable
        );

        return accountsPage.map(mapper::toDto);
    }

    private void validateAdmin(String adminId) {
        UserDto admin = usersFeignClient.getUser(adminId).getBody();
        if (admin == null || !admin.getRoles().contains(UserRole.ROLE_ADMIN)) {
            throw new IllegalArgumentException("Invalid admin ID: " + adminId);
        }
    }
}