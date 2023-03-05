package com.doka.customer.entity;

import com.doka.customer.util.Const;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Setter
@Getter
@ToString
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_seq")
    @SequenceGenerator(name = "account_id_seq", sequenceName = "account_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    private String iban;

    private BigDecimal balance;

    @Column(name = "create_date",
            columnDefinition = Const.Definition.TIMESTAMP_WITH_TIMEZONE,
            insertable = false, updatable = false
    )
    private LocalDateTime createDate;

}
