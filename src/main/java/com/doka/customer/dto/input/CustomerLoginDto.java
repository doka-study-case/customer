package com.doka.customer.dto.input;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
public class CustomerLoginDto {

    @Positive
    private Long id;

    @NotEmpty
    private String password;

}
