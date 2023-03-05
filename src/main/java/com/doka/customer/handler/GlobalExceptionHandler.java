package com.doka.customer.handler;

import com.doka.customer.dto.output.ApiResponseDto;
import com.doka.customer.exception.DokaException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Validation hatalarını yakalar ve uygun bir hata mesajı döner.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto> handleValidationErrors(MethodArgumentNotValidException exception) {
        Optional<String> firstErrorMessage = exception.getBindingResult()
                .getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst();

        Optional<FieldError> fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst();

        String message = "Validation error.";
        if (fieldError.isPresent()) {
            message = fieldError.get().toString();
        } else if (firstErrorMessage.isPresent()) {
            message = firstErrorMessage.get();
        }

        ApiResponseDto apiResponse = new ApiResponseDto(message);

        return ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Geri kalan tüm exception'ları yakalar.
     */
    @ExceptionHandler
    public ResponseEntity<ApiResponseDto> handleAllOtherExceptions(Throwable throwable) {
        ApiResponseDto apiResponse = new ApiResponseDto(throwable.getMessage());
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (throwable.getCause() instanceof DokaException) {
            DokaException dokaException = (DokaException) throwable.getCause();
            apiResponse.setMessage(dokaException.getErrorMessage());
            httpStatus = dokaException.getErrorCode();
        }

        return ResponseEntity
                .status(httpStatus)
                .body(apiResponse);
    }

}
