package com.ylli.transactions_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.models.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoansMapper extends BaseMapper<Loan, LoanDto> {

    @Mapping(target = "accountId", source = "account.id")
    @Override
    LoanDto toDto(Loan loan);

    @Mapping(target = "account.id", source = "loanDto.accountId")
    @Override
    Loan toEntity(LoanDto loanDto);
}
