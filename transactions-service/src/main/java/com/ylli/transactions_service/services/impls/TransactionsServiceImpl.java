package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.AuditHelper;
import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.enums.*;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import com.ylli.transactions_service.configs.AdminTransactionSpecifications;
import com.ylli.transactions_service.configs.TransactionSpecifications;
import com.ylli.transactions_service.mappers.TransactionMapper;
import com.ylli.transactions_service.repositories.TransactionsRepository;
import com.ylli.transactions_service.services.TransactionsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionsServiceImpl extends BaseServiceImpl<Transaction, TransactionDto, String, TransactionsRepository, TransactionMapper> implements TransactionsService {
    private final AccountsFeignClient accountsFeignClient;
    private final UsersFeignClient usersFeignClient;
    private final AuditHelper auditHelper;

    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, TransactionMapper transactionMapper, AccountsFeignClient accountsFeignClient, UsersFeignClient usersFeignClient, AuditHelper auditHelper){
        super(transactionsRepository, transactionMapper);
        this.accountsFeignClient = accountsFeignClient;
        this.usersFeignClient = usersFeignClient;
        this.auditHelper = auditHelper;
    }

    @Override
    public List<TransactionDto> getUserTransactions(String userId) {
        List<AccountDto> accounts = accountsFeignClient.getUserAccounts(userId).getBody();

        List<Transaction> transactions = new ArrayList<>();
        if (accounts == null || accounts.isEmpty()) {
            return List.of();
        }
        for (AccountDto account : accounts) {
            var tempAccount = new Account();
            tempAccount.setId(account.getId());
            transactions.addAll(repository.findByAccount(tempAccount));
        }

        if (transactions.isEmpty()) {
            return null;
        }
        return mapper.toDtoList(transactions);

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
            throw new IllegalArgumentException("Source account ID is required.");
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

                auditHelper.createAudit(
                        AuditType.DEPOSIT_MADE,
                        "Deposit of " + newTransaction.getAmount() + " to account " + sourceAccountDto.getId(),
                        sourceAccountDto.getId()
                );

                break;

            case WITHDRAWAL:
                if (sourceAccountDto.getBalance().compareTo(newTransaction.getAmount()) < 0) {
                    newTransaction.setStatus(TransactionStatus.FAILED);
                    repository.save(newTransaction);
                    throw new IllegalStateException("Insufficient funds in source account for withdrawal.");
                }
                sourceAccountDto.setBalance(sourceAccountDto.getBalance().subtract(newTransaction.getAmount()));
                newTransaction.setStatus(TransactionStatus.COMPLETED);
                newTransaction.setRecipientAccount(null);

                auditHelper.createAudit(
                        AuditType.WITHDRAWAL_MADE,
                        "Withdrawal of " + newTransaction.getAmount() + " from account " + sourceAccountDto.getId(),
                        sourceAccountDto.getId()
                );

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
                    throw new IllegalArgumentException("Recipient account not found.");
                }
                if (recipientAccountDto.getStatus() != AccountStatus.ACTIVE) {
                    throw new IllegalStateException("Recipient account is not active.");
                }

                if (sourceAccountDto.getBalance().compareTo(newTransaction.getAmount()) < 0) {
                    newTransaction.setStatus(TransactionStatus.FAILED);
                    repository.save(newTransaction);
                    throw new IllegalStateException("Insufficient funds in source account for transfer.");
                }

                sourceAccountDto.setBalance(sourceAccountDto.getBalance().subtract(newTransaction.getAmount()));
                recipientAccountDto.setBalance(recipientAccountDto.getBalance().add(newTransaction.getAmount()));

                Account recipientAccountEntity = new Account();
                recipientAccountEntity.setId(recipientAccountDto.getId());
                newTransaction.setRecipientAccount(recipientAccountEntity);

                newTransaction.setStatus(TransactionStatus.COMPLETED);

                accountsFeignClient.update(recipientAccountDto.getId(), recipientAccountDto);

                auditHelper.createAudit(
                        AuditType.MONEY_TRANSFERRED,
                        "Transfer of " + newTransaction.getAmount() + " from account " + sourceAccountDto.getId() +
                                " to account " + recipientAccountDto.getId(),
                        sourceAccountDto.getId()
                );

                break;

            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + newTransaction.getType());
        }

        accountsFeignClient.update(sourceAccountDto.getId(), sourceAccountDto);

        Transaction savedTransaction = repository.save(newTransaction);

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
            throw new IllegalStateException("Recipient does not have enough balance to revert transfer");
        }

        recipient.setBalance(recipient.getBalance().subtract(original.getAmount()));
        sender.setBalance(sender.getBalance().add(original.getAmount()));

        accountsFeignClient.update(sender.getId(), sender);
        accountsFeignClient.update(recipient.getId(), recipient);

        original.setStatus(TransactionStatus.REVERSED);
        repository.save(original);

//        Transaction reversal = new Transaction();
//        reversal.setAccount(original.getAccount());
//        reversal.setRecipientAccount(original.getRecipientAccount());
//        reversal.setAmount(original.getAmount());
//        reversal.setType(TransactionType.TRANSFER);
//        reversal.setStatus(TransactionStatus.COMPLETED);
//        reversal.setDetails("Reversal of transaction " + transactionId);
//        reversal.setCreatedAt(LocalDateTime.now());
//
//        repository.save(reversal);

        return mapper.toDto(original);
    }

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
            throw new IllegalArgumentException("User with ID " + adminId + " is not an admin.");
        }
    }

}
