package com.ylli.accounts_service.services.impls;

import com.ylli.accounts_service.repositories.AccountsRepository;
import com.ylli.accounts_service.services.AccountsService;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.accounts_service.mappers.AccountMapper;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
public class AccountsServiceImpl extends BaseServiceImpl<Account, AccountDto, String, AccountsRepository, AccountMapper> implements AccountsService {


    @Autowired
    public AccountsServiceImpl(AccountsRepository repository, AccountMapper mapper) {
        super(repository, mapper);
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