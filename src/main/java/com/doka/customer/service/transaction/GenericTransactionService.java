package com.doka.customer.service.transaction;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.enums.CustomerType;

import java.math.BigDecimal;

public interface GenericTransactionService {

    BigDecimal calculateFee(CustomerType customerType, BigDecimal amount);

    void makeTransfer(Long customerId, CustomerType customerType, TransactionDto transactionDto);

    void makeTransfer(CustomerType customerType,
                      AccountEntity sourceAccountEntity, AccountEntity targetAccountEntity,
                      TransactionDto transactionDto);

}
