package com.doka.customer.dto.output;

import com.doka.customer.enums.CustomerType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerOutputDto {

    private Long id;
    private String name;
    private String address;
    private CustomerType type;
    private LocalDateTime registerDate;

}
