package com.doka.customer.service;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.enums.CustomerType;

import java.math.BigDecimal;

public interface GenericTransaction {

    /**
     * Transaction fee ratio depends on customer type
     */
    float getFeeRatio(CustomerType customerType);

    BigDecimal calculateFee(CustomerType customerType, BigDecimal amount);

    void makeTransfer(Long customerId, CustomerType customerType, TransactionDto transactionDto);

}
