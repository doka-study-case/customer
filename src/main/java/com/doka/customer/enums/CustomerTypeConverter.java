package com.doka.customer.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class CustomerTypeConverter implements AttributeConverter<CustomerType, String> {
    @Override
    public String convertToDatabaseColumn(CustomerType attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.toString();
    }

    @Override
    public CustomerType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(CustomerType.values())
                .filter(type -> type.toString().equalsIgnoreCase(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
