package com.ylli.admin_service.services.impls;

import com.ylli.admin_service.services.AdminAccountsService;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.enums.AccountStatus;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAccountsServiceImpl implements AdminAccountsService {

    private final AccountsFeignClient accountsFeignClient;

    @Autowired
    public AdminAccountsServiceImpl(AccountsFeignClient accountsFeignClient) {
        this.accountsFeignClient = accountsFeignClient;
    }


    @Override
    public List<AccountDto> getAllAccounts() {
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
    public Boolean freezeAccount(String accountId) {
        try {
            HttpStatusCode code = accountsFeignClient.freezeAccountFromAdmin(accountId).getStatusCode();
            if (code.is2xxSuccessful()) {
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
    public Boolean unfreezeAccount(String accountId) {
        try {
            HttpStatusCode code = accountsFeignClient.unfreezeAccount(accountId).getStatusCode();
            if (code.is2xxSuccessful()) {
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
    public void approveAccount(String accountId) {
        try {
            AccountDto account = accountsFeignClient.getById(accountId).getBody();
            if (account == null) {
                throw new IllegalArgumentException("Account with ID " + accountId + " not found");
            }
            if (account.getStatus() != AccountStatus.PENDING_APPROVAL){
                throw new IllegalArgumentException("Account with ID " + accountId + " is not pending approval");
            }
            account.setStatus(AccountStatus.ACTIVE);
            accountsFeignClient.update(accountId, account);
        }catch (FeignException e){
            throw new IllegalArgumentException("Error approving account: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while approving the account: " + e.getMessage(), e);
        }
    }
}
