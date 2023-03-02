package com.doka.customer.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class DokaException extends Exception {

    private int errorCode = HttpStatus.SERVICE_UNAVAILABLE.value();

    public DokaException(String message) {
        super(message);
    }

    public DokaException(String message, int errorCode) {
        super(message);

        this.errorCode = errorCode;
    }

}
