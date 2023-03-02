package com.doka.customer.enums;

import org.hibernate.annotations.TypeDef;

@TypeDef(name = "customer_type", typeClass = CustomerTypeConverter.class)
public enum CustomerType {
    INDIVIDUAL,
    BUSINESS;
}
