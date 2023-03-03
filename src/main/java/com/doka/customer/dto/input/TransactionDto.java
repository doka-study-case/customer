package com.doka.customer.dto.input;

import com.doka.customer.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class TransactionDto {

    @NotNull
    private TransactionType type;

    @NotNull
    @Positive
    private BigDecimal amount;

    /**
     * müşterinin para transferi yapılacak hesap id'sidir
     */
    @NotNull
    private Long accountId;

    /**
     * transfer yapılacak müşterinin hesabının iban numarası
     */
    private String iban;

    /**
     * kurum ödemesi yapılıyorsa ödeme yapılan kurum adıdır. (başkentgaz, enerjisa vs)
     */
    private String corporation;

}
