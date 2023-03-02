package com.doka.customer.dto.query;

import com.doka.customer.util.Const;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.Min;

@Data
@EqualsAndHashCode
public class PaginationQueryDto {

    @Min(0)
    private int page = 0;

    @Min(1)
    private int limit = Const.Default.LIST_ITEM_SIZE;

    public PageRequest toPageable() {
        return PageRequest.of(page, limit);
    }

}
