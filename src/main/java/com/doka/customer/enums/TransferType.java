package com.doka.customer.enums;

import org.hibernate.annotations.TypeDef;

@TypeDef(name = "transfer_type", typeClass = TransferTypeConverter.class)
public enum TransferType {
    INCOMING_EFT,
    OUTGOING_EFT,
    BILL_PAYMENT,
    TRANSACTION_FEE,
    OUTGOING_SALARY_PAYMENT,
    INCOMING_SALARY_PAYMENT
}
