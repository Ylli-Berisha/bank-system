package com.ylli.transactions_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.models.Loan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoansMapper extends BaseMapper<Loan, LoanDto> {
}
