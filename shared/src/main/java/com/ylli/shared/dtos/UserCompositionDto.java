package com.ylli.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCompositionDto {
    UserDto user;
    Page<AccountDto> accounts;
    Page<TransactionDto> transactions;
    Page<LoanDto> loans;
}
