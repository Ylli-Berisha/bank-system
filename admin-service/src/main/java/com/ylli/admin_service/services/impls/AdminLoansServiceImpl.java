package com.ylli.admin_service.services.impls;

import com.ylli.admin_service.services.AdminLoansService;
import com.ylli.shared.base.AuditHelper;
import com.ylli.shared.clients.AccountsFeignClient;
import com.ylli.shared.clients.AuditFeignClient;
import com.ylli.shared.clients.TransactionsFeignClient;
import com.ylli.shared.clients.UsersFeignClient;
import com.ylli.shared.dtos.AuditDto;
import com.ylli.shared.dtos.LoanChangeProposalRequestDto;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.enums.AuditType;
import com.ylli.shared.enums.LoanStatus;
import com.ylli.shared.enums.UserRole;
import com.ylli.shared.exceptions.BadGatewayException;
import com.ylli.shared.exceptions.InvalidRoleException;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

@Service
public class AdminLoansServiceImpl implements AdminLoansService {

    private final static Logger log = LoggerFactory.getLogger(AdminLoansServiceImpl.class);
    private final TransactionsFeignClient transactionsFeignClient;
    private final UsersFeignClient usersFeignClient;
    private final AuditHelper auditHelper;
    private final AccountsFeignClient accountsFeignClient;

    public AdminLoansServiceImpl(TransactionsFeignClient transactionsFeignClient, UsersFeignClient usersFeignClient, AuditHelper auditHelper, AccountsFeignClient accountsFeignClient) {
        this.transactionsFeignClient = transactionsFeignClient;
        this.usersFeignClient = usersFeignClient;
        this.auditHelper = auditHelper;
        this.accountsFeignClient = accountsFeignClient;
    }

    @Override
    public Page<LoanDto> getFilteredLoans(String adminId, String userId, String username, String email, String typeString, String statusString, String startDate, String endDate, BigDecimal minAmount, BigDecimal maxAmount, int page, int size) {
        try {
            return transactionsFeignClient.filterAdminLoans( adminId,  userId,  username,  email,  typeString,  statusString,  startDate,  endDate,  minAmount,  maxAmount,
             page,  size
            ).getBody();
        } catch (FeignException e){
            log.error("Error fetching filtered loans: {}", e.getMessage(), e);
            throw new BadGatewayException("Error fetching filtered loans: " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public LoanDto acceptLoan(Long loanId, String adminId) {
        try {
            LoanDto loanDto = transactionsFeignClient.acceptLoan(adminId, loanId).getBody();

            if (loanDto == null) {
                throw new ResourceNotFoundException("Loan with ID " + loanId + " not found.");
            }

            auditHelper.createAudit(AuditType.LOAN_APPROVED,
                    "Loan with ID " + loanId + " has been approved by admin with ID " + adminId,
                    loanDto.getAccountId());

            return loanDto;

        } catch (FeignException e) {
            log.error("Error accepting loan ID {}: {}", loanId, e.getMessage(), e);
            throw new BadGatewayException("Error accepting loan: " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred while accepting loan: " + e.getMessage(), e);
        }
    }

    @Override
    public LoanDto rejectLoan(Long loanId, String adminId) {
        try {
            LoanDto loanDto = transactionsFeignClient.rejectLoan(adminId, loanId).getBody();
            if (loanDto == null) {
                throw new ResourceNotFoundException("Loan with ID " + loanId + " not found.");
            }

            auditHelper.createAudit(AuditType.LOAN_DECLINED,
                    "Loan with ID " + loanId + " has been rejected by admin with ID " + adminId,
                    loanDto.getAccountId());

            return loanDto;

        } catch (FeignException e) {
            log.error("Error rejecting loan ID {}: {}", loanId, e.getMessage(), e);
            throw new BadGatewayException("Error rejecting loan: " + e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred while rejecting loan: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public LoanDto proposeChangesForLoan(String adminId, Long loanId, LoanChangeProposalRequestDto proposalDto) {
        validateAdmin(adminId);
        try {
            LoanDto loan = transactionsFeignClient.getLoan(loanId).getBody();
            if (loan == null) {
                throw new ResourceNotFoundException("Loan with ID " + loanId + " not found.");
            }

            if (loan.getStatus() != LoanStatus.PENDING) {
                throw new IllegalStateException("Changes can only be proposed for loans in PENDING status.");
            }

            loan.setAmount(proposalDto.getProposedAmount());
            loan.setTermInMonths(proposalDto.getProposedTermInMonths());
            loan.setInterestRate(proposalDto.getProposedInterestRate());

            loan.setStatus(LoanStatus.CHANGES_PROPOSED);

            var account = accountsFeignClient.getAccount(loan.getAccountId()).getBody();

            transactionsFeignClient.updateLoan(loan.getId(), loan);
            assert account != null;
            transactionsFeignClient.evictUserLoansCache(adminId, account.getUserId());

            auditHelper.createAudit(AuditType.LOAN_CHANGES_PROPOSED,
                    "Changes proposed for loan with ID " + loanId + " by admin with ID " + adminId,
                    loan.getAccountId());

            return loan;
        } catch (ResourceNotFoundException | IllegalStateException e) {
            log.warn("Failed to propose changes for loan ID {}. Error: {}", loanId, e.getMessage());
            throw e;
        } catch (FeignException e) {
            log.error("Error proposing changes for loan ID {}: {}", loanId, e.getMessage(), e);
            throw new BadGatewayException("Error proposing changes for loan: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("Unexpected error while proposing changes for loan ID {}. Error: {}", loanId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error while proposing loan changes.", e);
        }
    }

    public void validateAdmin(String userId) {
        var user = usersFeignClient.getUser(userId).getBody();
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have any roles");
        }
        if (!user.getRoles().contains(UserRole.ROLE_ADMIN)) {
            throw new InvalidRoleException("User with ID " + userId + " is not an admin");
        }
    }

}
