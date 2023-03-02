package com.doka.customer.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class AccountCreateDto {

    @JsonProperty("initialBalance")
    private BigDecimal balance;

    @Pattern(regexp = "(TR)(\\d{16})", message = "sample input: TR1234567890123456")
    @NotNull
    private String iban;

}
