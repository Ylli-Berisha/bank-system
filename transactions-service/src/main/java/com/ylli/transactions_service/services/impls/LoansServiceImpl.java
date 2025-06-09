package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.dtos.AccountDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.models.Account;
import com.ylli.shared.models.Loan;
import com.ylli.shared.models.Transaction;
import com.ylli.shared.models.User;
import com.ylli.transactions_service.mappers.LoansMapper;
import com.ylli.transactions_service.repositories.LoansRepository;
import com.ylli.transactions_service.services.LoansService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoansServiceImpl extends BaseServiceImpl<Loan, LoanDto, Long, LoansRepository, LoansMapper> implements LoansService {

    private final AccountsFeignClient accountsFeignClient;

    public LoansServiceImpl(LoansRepository repository, LoansMapper mapper, AccountsFeignClient accountsFeignClient) {
        super(repository, mapper);
        this.accountsFeignClient = accountsFeignClient;
    }

    @Override
    public List<LoanDto> getUserLoans(String userId) {
        List<AccountDto> accounts = accountsFeignClient.getUserAccounts(userId).getBody();

        List<Loan> loans = new ArrayList<>();
        if (accounts == null || accounts.isEmpty()) {
            throw new EntityNotFoundException("User with ID " + userId + " has no accounts.");
        }
        for (AccountDto account : accounts) {
            var tempAccount = new Account();
            tempAccount.setId(account.getId());
            loans.addAll(repository.findByAccount(tempAccount));
        }

        if (loans.isEmpty()) {
            return List.of();
        } else {
            return mapper.toDtoList(loans);
        }
    }
}
