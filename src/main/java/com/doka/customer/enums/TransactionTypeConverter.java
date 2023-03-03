package com.doka.customer.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionType, String> {
    @Override
    public String convertToDatabaseColumn(TransactionType attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.toString();
    }

    @Override
    public TransactionType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(TransactionType.values())
                .filter(type -> type.toString().equalsIgnoreCase(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
