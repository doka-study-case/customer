package com.doka.customer.dto.input;

import com.doka.customer.enums.CustomerType;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class CustomerRegisterDto {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotNull
    private CustomerType type;

    @NotBlank
    @Size(min = 6)
    private String password;

}
