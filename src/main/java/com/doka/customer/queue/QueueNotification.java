package com.doka.customer.queue;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class QueueNotification {

    private Long customerId;
    private String message;
    private LocalDateTime notificationDate;

}
