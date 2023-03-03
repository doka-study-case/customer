package com.doka.customer.enums;

import org.hibernate.annotations.TypeDef;

@TypeDef(name = "transaction_type", typeClass = TransactionTypeConverter.class)
public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    EFT,
    BILL_PAYMENT,
    SALARY_PAYMENT
}