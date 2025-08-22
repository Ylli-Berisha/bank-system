package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.AuditHelper;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.enums.*;
import com.ylli.shared.exceptions.*;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Transaction;
import com.ylli.transactions_service.configs.AdminTransactionSpecifications;
import com.ylli.transactions_service.configs.TransactionSpecifications;
import com.ylli.transactions_service.mappers.TransactionMapper;
import com.ylli.transactions_service.repositories.TransactionsRepository;
import com.ylli.transactions_service.services.TransactionsService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionsServiceImpl extends BaseServiceImpl<Transaction, TransactionDto, String, TransactionsRepository, TransactionMapper> implements TransactionsService {

    private final AccountsFeignClient accountsFeignClient;
    private final UsersFeignClient usersFeignClient;
    private final AuditHelper auditHelper;
    private final CacheManager cacheManager;
    private static final Logger log = LoggerFactory.getLogger(TransactionsServiceImpl.class);

    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, TransactionMapper transactionMapper, AccountsFeignClient accountsFeignClient, UsersFeignClient usersFeignClient, AuditHelper auditHelper, CacheManager cacheManager){
        super(transactionsRepository, transactionMapper);
        this.accountsFeignClient = accountsFeignClient;
        this.usersFeignClient = usersFeignClient;
        this.auditHelper = auditHelper;
        this.cacheManager = cacheManager;
    }

    @Cacheable(
            value = "userTransactions",
            key = "#userId + '-' + #page + '-' + #size"
    )
    @Override
    public Page<TransactionDto> getUserTransactions(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<AccountDto> accounts = List.of();
        try {
            accounts = accountsFeignClient.getUserAccounts2(userId).getBody();
        }
        catch (FeignException e){
            log.error(e.getMessage());
            throw new BadGatewayException("Error fetching user accounts: " + e.getMessage());
        }
        if (accounts == null || accounts.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Account> accountEntities = accounts.stream()
                .map(dto -> {
                    Account a = new Account();
                    a.setId(dto.getId());
                    return a;
                })
                .collect(Collectors.toList());

        Page<Transaction> transactionPage = repository.findByAccountIn(accountEntities, pageable);
        List<TransactionDto> transactionDtos = mapper.toDtoList(transactionPage.getContent());

        return new PageImpl<>(transactionDtos, pageable, transactionPage.getTotalElements());
    }

    @Override
    public List<TransactionDto> filterUserTransactions(
            String userId,
            String typeString,
            String statusString,
            String startDate,
            String endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String query
    ) {
        LocalDateTime parsedStartDateTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            parsedStartDateTime = LocalDate.parse(startDate).atStartOfDay();
        }

        LocalDateTime parsedEndDateTime = null;
        if (endDate != null && !endDate.isEmpty()) {
            parsedEndDateTime = LocalDate.parse(endDate).atTime(23, 59, 59, 999999999);
        }

        TransactionStatus parsedStatus = null;
        if (statusString != null && !statusString.isEmpty()) {
            try {
                System.err.println("Warning: Received invalid TransactionStatus string: " + statusString);
                parsedStatus = TransactionStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid TransactionStatus string: " + statusString);
                throw e;
            }
        }

        TransactionType parsedType = null;
        if (typeString != null && !typeString.isEmpty()) {
            try {
                parsedType = TransactionType.valueOf(typeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid TransactionType string: " + typeString);
                throw e;
            }
        }

        BigDecimal actualMinAmount = (minAmount != null) ? minAmount : BigDecimal.ZERO;
        BigDecimal actualMaxAmount = (maxAmount != null) ? maxAmount : new BigDecimal("999999999999999.99");

        List<Transaction> transactions = repository.findAll(
                TransactionSpecifications.withFilters(
                        userId,
                        parsedType,
                        parsedStatus,
                        parsedStartDateTime,
                        parsedEndDateTime,
                        actualMinAmount,
                        actualMaxAmount,
                        query
                )
        );

        return mapper.toDtoList(transactions);
    }

    @Override
    public Page<TransactionDto> filterAdminTransactions(
            String adminId,
            String userId,
            String username,
            String email,
            String typeString,
            String statusString,
            String startDate,
            String endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String query,
            int page,
            int size
    ) {
        validateAdmin(adminId);

        LocalDateTime parsedStartDateTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            parsedStartDateTime = LocalDate.parse(startDate).atStartOfDay();
        }

        LocalDateTime parsedEndDateTime = null;
        if (endDate != null && !endDate.isEmpty()) {
            parsedEndDateTime = LocalDate.parse(endDate).atTime(23, 59, 59, 999999999);
        }

        TransactionStatus parsedStatus = null;
        if (statusString != null && !statusString.isEmpty()) {
            try {
                System.err.println("Warning: Received invalid TransactionStatus string: " + statusString);
                parsedStatus = TransactionStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid TransactionStatus string: " + statusString);
                throw e;
            }
        }

        TransactionType parsedType = null;
        if (typeString != null && !typeString.isEmpty()) {
            try {
                parsedType = TransactionType.valueOf(typeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Received invalid TransactionType string: " + typeString);
                throw e;
            }
        }

        BigDecimal actualMinAmount = (minAmount != null) ? minAmount : BigDecimal.ZERO;
        BigDecimal actualMaxAmount = (maxAmount != null) ? maxAmount : new BigDecimal("999999999999999.99");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Transaction> transactionsPage = repository.findAll(
                AdminTransactionSpecifications.withFilters(
                        userId,
                        username,
                        email,
                        parsedType,
                        parsedStatus,
                        parsedStartDateTime,
                        parsedEndDateTime,
                        actualMinAmount,
                        actualMaxAmount,
                        query
                ),
                pageable
        );

        return transactionsPage.map(mapper::toDto);
    }

    @Override
    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto, String userId) {
        String sourceAccountId = transactionDto.getAccountId();
        if (sourceAccountId == null || sourceAccountId.isBlank()) {
            throw new ResourceDoesNotMatchException("Source account ID is required.");
        }

        AccountDto sourceAccountDto = accountsFeignClient.getAccountByIdAndUserId(sourceAccountId, userId).getBody();
        if (sourceAccountDto == null) {
            throw new IllegalArgumentException("Source account not found or does not belong to the authenticated user.");
        }
        if (sourceAccountDto.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Source account is not active.");
        }

        if (transactionDto.getAmount() == null || transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive.");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setType(transactionDto.getType());
        newTransaction.setAmount(transactionDto.getAmount());
        newTransaction.setDetails(transactionDto.getDetails());

        Account sourceAccountEntity = new Account();
        sourceAccountEntity.setId(sourceAccountDto.getId());
        newTransaction.setAccount(sourceAccountEntity);

        newTransaction.setCreatedAt(LocalDateTime.now());

        switch (newTransaction.getType()) {
            case DEPOSIT:
                sourceAccountDto.setBalance(sourceAccountDto.getBalance().add(newTransaction.getAmount()));
                newTransaction.setStatus(TransactionStatus.COMPLETED);
                newTransaction.setRecipientAccount(null);
                auditHelper.createAudit(AuditType.DEPOSIT_MADE, "Deposit of " + newTransaction.getAmount() + " to account " + sourceAccountDto.getId(), sourceAccountDto.getId());
                break;

            case WITHDRAWAL:
                if (sourceAccountDto.getBalance().compareTo(newTransaction.getAmount()) < 0) {
                    newTransaction.setStatus(TransactionStatus.FAILED);
                    repository.save(newTransaction);
                    throw new InsufficientFundsException("Insufficient funds in source account for withdrawal.");
                }
                sourceAccountDto.setBalance(sourceAccountDto.getBalance().subtract(newTransaction.getAmount()));
                newTransaction.setStatus(TransactionStatus.COMPLETED);
                newTransaction.setRecipientAccount(null);
                auditHelper.createAudit(AuditType.WITHDRAWAL_MADE, "Withdrawal of " + newTransaction.getAmount() + " from account " + sourceAccountDto.getId(), sourceAccountDto.getId());
                break;

            case TRANSFER:
                String recipientAccountId = transactionDto.getRecipientAccountId();
                if (recipientAccountId == null || recipientAccountId.isBlank()) {
                    throw new IllegalArgumentException("Recipient account ID is required for transfers.");
                }
                if (sourceAccountId.equals(recipientAccountId)) {
                    throw new IllegalArgumentException("Cannot transfer to the same account.");
                }

                AccountDto recipientAccountDto = accountsFeignClient.getById(recipientAccountId).getBody();
                if (recipientAccountDto == null) {
                    throw new BadGatewayException("Recipient account not found.");
                }
                if (recipientAccountDto.getStatus() != AccountStatus.ACTIVE) {
                    throw new AccountLockedException("Recipient account is not active.");
                }

                if (sourceAccountDto.getBalance().compareTo(newTransaction.getAmount()) < 0) {
                    newTransaction.setStatus(TransactionStatus.FAILED);
                    repository.save(newTransaction);
                    throw new InsufficientFundsException("Insufficient funds in source account for transfer.");
                }

                sourceAccountDto.setBalance(sourceAccountDto.getBalance().subtract(newTransaction.getAmount()));
                recipientAccountDto.setBalance(recipientAccountDto.getBalance().add(newTransaction.getAmount()));

                Account recipientAccountEntity = new Account();
                recipientAccountEntity.setId(recipientAccountDto.getId());
                newTransaction.setRecipientAccount(recipientAccountEntity);

                newTransaction.setStatus(TransactionStatus.COMPLETED);

                accountsFeignClient.update(recipientAccountDto.getId(), recipientAccountDto);

                auditHelper.createAudit(AuditType.MONEY_TRANSFERRED, "Transfer of " + newTransaction.getAmount() + " from account " + sourceAccountDto.getId() + " to account " + recipientAccountDto.getId(), sourceAccountDto.getId());
                break;

            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + newTransaction.getType());
        }

        accountsFeignClient.update(sourceAccountDto.getId(), sourceAccountDto);
        Transaction savedTransaction = repository.save(newTransaction);

        evictUserTransactionsCache(userId);
        if (newTransaction.getType() == TransactionType.TRANSFER && newTransaction.getRecipientAccount() != null) {
            String recipientUserId = getUserFromAccountId(newTransaction.getRecipientAccount().getId());
            if (recipientUserId != null && !userId.equals(recipientUserId)) {
                evictUserTransactionsCache(recipientUserId);
            }
        }

        return mapper.toDto(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDto revertTransaction(String transactionId, String adminId) {
        validateAdmin(adminId);

        Transaction original = repository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (original.getType() != TransactionType.TRANSFER || original.getStatus() != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Only completed transfers can be reverted");
        }

        AccountDto sender = accountsFeignClient.getById(original.getAccount().getId()).getBody();
        AccountDto recipient = accountsFeignClient.getById(original.getRecipientAccount().getId()).getBody();

        assert recipient != null;
        if (recipient.getBalance().compareTo(original.getAmount()) < 0) {
            throw new InsufficientFundsException("Recipient does not have enough balance to revert transfer");
        }

        recipient.setBalance(recipient.getBalance().subtract(original.getAmount()));
        sender.setBalance(sender.getBalance().add(original.getAmount()));

        accountsFeignClient.update(sender.getId(), sender);
        accountsFeignClient.update(recipient.getId(), recipient);

        original.setStatus(TransactionStatus.REVERSED);
        repository.save(original);

        String senderUserId = getUserFromAccountId(original.getAccount().getId());
        if (senderUserId != null) {
            evictUserTransactionsCache(senderUserId);
        }
        String recipientUserId = getUserFromAccountId(original.getRecipientAccount().getId());
        if (recipientUserId != null && !recipientUserId.equals(senderUserId)) {
            evictUserTransactionsCache(recipientUserId);
        }

        return mapper.toDto(original);
    }

    @Cacheable(
            value = "topUserTransactions",
            key = "#userId"
    )
    @Override
    public List<TransactionDto> getTopUserTransactions(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }
        var user = usersFeignClient.getUser(userId).getBody();
        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        }

        Pageable pageable = PageRequest.of(0, 4);
        List<Transaction> latest4UserTx = repository.findTop4ByUserIdOrderByCreatedAtDesc(userId, pageable);
        if (latest4UserTx.isEmpty()) {
            return List.of();
        }
        return mapper.toDtoList(latest4UserTx);
    }

    private void validateAdmin(String adminId){
        if (adminId == null || adminId.isBlank()) {
            throw new IllegalArgumentException("Admin ID is required.");
        }
        var adminUser = usersFeignClient.getUser(adminId).getBody();
        if (adminUser == null || !adminUser.getRoles().contains(UserRole.ROLE_ADMIN)) {
            throw new InvalidRoleException("User with ID " + adminId + " is not an admin.");
        }
    }

    public String getUserFromAccountId(String accountId) {
        if (accountId == null) {
            return null;
        }
        AccountDto accountDto = accountsFeignClient.getById(accountId).getBody();
        if (accountDto != null) {
            return accountDto.getUserId();
        }
        return null;
    }

    private void evictUserTransactionsCache(String userId) {
        Cache userTransactionsCache = cacheManager.getCache("userTransactions");
        Cache topUserTransactionsCache = cacheManager.getCache("topUserTransactions");

        if (userTransactionsCache == null) {
            log.warn("Cache 'userTransactions' not found, skipping eviction for user {}", userId);
        } else {
            List<Integer> commonPageSizes = List.of(6, 10, 20, 50, 100);
            int maxPagesToClear = 5;

            for (int pageSize : commonPageSizes) {
                for (int page = 0; page < maxPagesToClear; page++) {
                    String keyToEvict = userId + "-" + page + "-" + pageSize;
                    userTransactionsCache.evict(keyToEvict);
                }
            }
            log.info("Evicted 'userTransactions' cache for user ID: {} across common pages/sizes.", userId);
        }

        if (topUserTransactionsCache != null) {
            topUserTransactionsCache.evict(userId);
            log.info("Evicted 'topUserTransactions' cache for user ID: {}", userId);
        } else {
            log.warn("Cache 'topUserTransactions' not found, skipping eviction for user {}", userId);
        }
    }
}
