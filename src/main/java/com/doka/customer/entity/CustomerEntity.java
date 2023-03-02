package com.doka.customer.entity;

import com.doka.customer.enums.CustomerType;
import com.doka.customer.util.Const;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_id_seq", allocationSize = 1)
    private Long id;

    private CustomerType type;

    private String name;
    private String address;

    @Column(name = "register_date", columnDefinition = Const.Definition.TIMESTAMP_WITH_TIMEZONE)
    private LocalDateTime registerDate;

    private String password;

}
