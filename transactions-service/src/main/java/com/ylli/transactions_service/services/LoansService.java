package com.ylli.transactions_service.services;

import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.models.Loan;

import java.util.List;

public interface LoansService extends BaseService<LoanDto, Long> {
    public List<LoanDto> getUserLoans(String userId);
}
