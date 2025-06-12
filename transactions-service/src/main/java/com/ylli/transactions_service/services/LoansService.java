package com.ylli.transactions_service.services;

import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.models.Loan;

import java.util.List;

public interface LoansService extends BaseService<LoanDto, Long> {
    List<LoanDto> getUserLoans(String userId, LoanStatus loanStatus);
    List<String> getLoanTypes();
    Boolean applyForLoan(String accountId, LoanApplicationRequestDto loanApplicationRequestDto, String userId);
}
