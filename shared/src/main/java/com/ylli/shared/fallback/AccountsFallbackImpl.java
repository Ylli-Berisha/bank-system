package com.ylli.shared.fallback;

import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.PageResponseDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class AccountsFallbackImpl implements FallbackFactory<AccountsFeignClient> {
    @Override
    public AccountsFeignClient create(Throwable cause) {
        return new AccountsFeignClient() {

            @Override
            public ResponseEntity<List<AccountDto>> getAll() {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AccountDto> getAccount(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AccountDto> createAccount(AccountDto account) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AccountDto> updateAccount(String id, AccountDto account) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<Void> deleteAccount(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AccountDto> getDefaultAccount() {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<Page<AccountDto>> getUserAccounts(String userId, Integer page, Integer size) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<List<AccountDto>> getUserAccounts2(String userId) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AccountDto> getAccountByIdAndUserId(String id, String userId) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AccountDto> getById(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AccountDto> update(String id, AccountDto dto) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<?> freezeAccount(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<?> unfreezeAccount(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<?> freezeAccountFromAdmin(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<PageResponseDto<AccountDto>> filterAdminAccounts(String adminId, String accountId, String userId, String username, String email, String loanId, String transactionId, String type, String status, BigDecimal minBalance, BigDecimal maxBalance, int page, int size) {
                return ResponseEntity.status(503).build();
            }
        };
    }
}
