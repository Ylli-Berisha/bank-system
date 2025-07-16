package com.ylli.transactions_service.services;

import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.models.Loan;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface LoansService extends BaseService<LoanDto, Long> {
    List<LoanDto> getUserLoans(String userId, LoanStatus loanStatus);

    List<String> getLoanTypes();

    Boolean applyForLoan(String accountId, LoanApplicationRequestDto loanApplicationRequestDto, String userId);

    List<LoanDto> filterUserLoans(String userId, String loanTypeString, String statusString, String startDateString, String endDateString, Double minAmount, Double maxAmount, String query);

    Page<LoanDto> filterAdminLoans(
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
            int page,
            int size
    );

    LoanDto acceptLoan(Long loanId, String adminId);

    LoanDto rejectLoan(Long loanId, String adminId);
}
