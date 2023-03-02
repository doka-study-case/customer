package com.doka.customer.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class DokaException extends Exception {

    private HttpStatus errorCode = HttpStatus.SERVICE_UNAVAILABLE;

    public DokaException(String message) {
        super(message);
    }

    public DokaException(String message, HttpStatus errorCode) {
        super(message);

        this.errorCode = errorCode;
    }

}
