package com.ylli.transactions_service.services.impls;

import com.ylli.shared.base.BaseServiceImpl;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.models.Loan;
import com.ylli.transactions_service.mappers.LoansMapper;
import com.ylli.transactions_service.repositories.LoansRepository;
import com.ylli.transactions_service.services.LoansService;
import org.springframework.stereotype.Service;

@Service
public class LoansServiceImpl extends BaseServiceImpl<Loan, LoanDto, Long, LoansRepository, LoansMapper> implements LoansService {
    public LoansServiceImpl(LoansRepository repository, LoansMapper mapper) {
        super(repository, mapper);
    }
}
