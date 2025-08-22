package com.ylli.transactions_service.services;

import com.ylli.shared.base.BaseService;
import com.ylli.shared.dtos.LoanApplicationRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.models.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface LoansService extends BaseService<LoanDto, Long> {
    Page<LoanDto> getUserLoans(String userId, LoanStatus status, int page, int size);

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

    LoanDto acceptProposedChanges(Long loanId, String userId);

    LoanDto rejectProposedChanges(Long loanId, String userId);

    List<LoanDto> getTopActiveLoans(String userId);

    void crossServiceEvictUserLoansCache(String adminId, String userId);
}
