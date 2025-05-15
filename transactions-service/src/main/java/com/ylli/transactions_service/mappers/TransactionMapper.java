package com.ylli.transactions_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.models.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends BaseMapper<Transaction, TransactionDto> {
}
