package com.doka.customer.entity;

import com.doka.customer.enums.TransactionType;
import com.doka.customer.util.Const;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_seq")
    @SequenceGenerator(name = "transaction_id_seq", sequenceName = "transaction_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private TransactionType type;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @NotNull
    private BigDecimal amount;

    @Column(name = "source_customer_id")
    private Long sourceCustomerId;

    @Column(name = "target_customer_id")
    private Long targetCustomerId;
    private String corporation;

    @Column(name = "transaction_date",
            columnDefinition = Const.Definition.TIMESTAMP_WITH_TIMEZONE,
            insertable = false, updatable = false
    )
    private LocalDateTime transactionDate;

}
