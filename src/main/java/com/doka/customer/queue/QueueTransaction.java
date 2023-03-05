package com.doka.customer.queue;

import com.doka.customer.enums.TransferType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class QueueTransaction {

    private Long customerId;
    private BigDecimal amount;
    private Long sourceAccountId;
    private Long targetAccountId;
    private String corporation;
    private TransferType transferType;
    private LocalDateTime transactionDate;

}
