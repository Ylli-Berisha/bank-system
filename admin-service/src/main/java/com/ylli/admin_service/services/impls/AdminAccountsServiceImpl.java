package com.ylli.admin_service.services.impls;

import com.ylli.admin_service.services.AdminAccountsService;
import com.ylli.shared.base.AuditHelper;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.AuditFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.dtos.PageResponseDto;
import com.ylli.shared.enums.AccountStatus;
import com.ylli.shared.enums.AuditType;
import com.ylli.shared.enums.UserRole;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class AdminAccountsServiceImpl implements AdminAccountsService {

    private final AccountsFeignClient accountsFeignClient;
    private final UsersFeignClient usersFeignClient;
    private final AuditHelper auditHelper;

    @Autowired
    public AdminAccountsServiceImpl(AccountsFeignClient accountsFeignClient, UsersFeignClient usersFeignClient, AuditHelper auditHelper) {
        this.accountsFeignClient = accountsFeignClient;
        this.usersFeignClient = usersFeignClient;
        this.auditHelper = auditHelper;
    }


    @Override
    public List<AccountDto> getAllAccounts(String userId) {
        validateAdmin(userId);
        try {
            List<AccountDto> accounts = accountsFeignClient.getAll().getBody();
            if (accounts == null || accounts.isEmpty()) {
                throw new IllegalArgumentException("No accounts found");
            }
            return accounts;
        } catch (FeignException e) {
            throw new IllegalArgumentException("Error fetching accounts: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while fetching accounts: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean freezeAccount(String accountId, String userId) {
        validateAdmin(userId);
        try {
            HttpStatusCode code = accountsFeignClient.freezeAccountFromAdmin(accountId).getStatusCode();
            if (code.is2xxSuccessful()) {

                auditHelper.createAudit(AuditType.ACCOUNT_LOCKED,
                        "Account with ID " + accountId + " has been frozen by admin: " + userId,
                                accountId);

                return true;
            } else {
                throw new IllegalArgumentException("Failed to freeze account with ID: " + accountId);
            }
        } catch (FeignException e) {
            throw new IllegalArgumentException("Error freezing account: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while freezing the account: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean unfreezeAccount(String accountId, String userId) {
        validateAdmin(userId);
        try {
            HttpStatusCode code = accountsFeignClient.unfreezeAccount(accountId).getStatusCode();
            if (code.is2xxSuccessful()) {

                auditHelper.createAudit(AuditType.ACCOUNT_UNLOCKED,
                        "Account with ID " + accountId + " has been unfrozen by admin: " + userId,
                                accountId);

                return true;
            } else {
                throw new IllegalArgumentException("Failed to unfreeze account with ID: " + accountId);
            }
        } catch (FeignException e) {
            throw new IllegalArgumentException("Error unfreezing account: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while unfreezing the account: " + e.getMessage(), e);
        }
    }

    @Override
    public void approveAccount(String accountId, String userId) {
        validateAdmin(userId);
        try {
            AccountDto account = accountsFeignClient.getById(accountId).getBody();
            if (account == null) {
                throw new IllegalArgumentException("Account with ID " + accountId + " not found");
            }
            if (account.getStatus() != AccountStatus.PENDING_APPROVAL) {
                throw new IllegalArgumentException("Account with ID " + accountId + " is not pending approval");
            }
            account.setStatus(AccountStatus.ACTIVE);
            accountsFeignClient.update(accountId, account);

            auditHelper.createAudit(AuditType.ACCOUNT_APPLICATION_APPROVED,
                    "Account with ID " + accountId + " has been approved by admin: " + userId,
                            accountId);

        } catch (FeignException e) {
            throw new IllegalArgumentException("Error approving account: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while approving the account: " + e.getMessage(), e);
        }
    }

    @Override
    public void rejectAccount(String accountId, String userId) {
        validateAdmin(userId);
        try {
            AccountDto account = accountsFeignClient.getById(accountId).getBody();
            if (account == null) {
                throw new ResourceNotFoundException("Account with ID " + accountId + " not found");
            }
            if (account.getStatus() != AccountStatus.PENDING_APPROVAL) {
                throw new IllegalStateException("Account with ID " + accountId + " is not pending approval");
            }
            account.setStatus(AccountStatus.REJECTED);
            accountsFeignClient.update(accountId, account);

            auditHelper.createAudit(AuditType.ACCOUNT_APPLICATION_REJECTED,
                    "Account with ID " + accountId + " has been rejected by admin: " + userId,
                            accountId);

        } catch (FeignException e) {
            throw new IllegalArgumentException("Error rejecting account: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while rejecting the account: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PageResponseDto<AccountDto> filterAdminAccounts(String adminId, String accountId, String typeString, BigDecimal minBalance, BigDecimal maxBalance, String statusString, String userId, String username, String email, String loanId, String transactionId, int page, int size) {
        try {
            return accountsFeignClient.filterAdminAccounts(adminId, accountId, userId, username, email, loanId, transactionId, typeString, statusString, minBalance, maxBalance, page, size).getBody();
        } catch (FeignException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("A feign error occurred while fetching transactions: " + e.getMessage(), e);
        }
    }

    public void validateAdmin(String userId) {
        var user = usersFeignClient.getUser(userId).getBody();
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have any roles");
        }
        if (!user.getRoles().contains(UserRole.ROLE_ADMIN)) {
            throw new IllegalArgumentException("User with ID " + userId + " is not an admin");
        }
    }
}
