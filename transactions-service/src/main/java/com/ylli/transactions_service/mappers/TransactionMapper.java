package com.ylli.transactions_service.mappers;

import com.ylli.shared.base.BaseMapper;
import com.ylli.shared.dtos.TransactionDto;
import com.ylli.shared.models.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends BaseMapper<Transaction, TransactionDto> {

    @Mapping(target = "accountId", source = "account.id")
    @Override
    TransactionDto toDto(Transaction transaction);

    @Mapping(target = "account.id", source = "transactionDto.accountId")
    @Override
    Transaction toEntity(TransactionDto transactionDto);
}
