package com.doka.customer.dto.query;

import com.doka.customer.enums.CustomerType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomersQueryDto extends PaginationQueryDto {

    private CustomerType type;

}
