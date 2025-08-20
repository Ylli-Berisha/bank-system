package com.ylli.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCompositionDto {
    PageResponseDto<AccountDto> activeAccounts;
    PageResponseDto<AccountDto> frozenAccounts;
    PageResponseDto<AccountDto> pendingAccounts;
    PageResponseDto<AccountDto> closedAccounts;
}
