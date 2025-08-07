package com.ylli.accounts_service.services;

import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AccountType;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface AccountsService extends BaseService<AccountDto, String> {

    List<AccountDto> getAll();

    Page<AccountDto> getUserAccounts(String userId, int page, int size);

//    AccountDto getDefaultAccount();

    List<String> getAccountTypes();

    List<String> getAccountStatuses();

    Boolean applyForNewAccount(AccountDto accountDto);

    Boolean freezeAccount(String accountId, String userId);

    Boolean unfreezeAccount(String accountId, String userId);

    AccountDto getByIdAndUserId(String id, String userId);

    List<AccountDto> getTopAccounts (String userId);

    Page<AccountDto> filterAdminAccounts(
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
    );

    Page<AccountDto> filterUserAccounts(String accountId, String userId, AccountType accountType, BigDecimal minBalance, BigDecimal maxBalance, AccountStatus accountStatus, int page, int size);
}
