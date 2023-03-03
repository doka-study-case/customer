package com.doka.customer.handler;

import com.doka.customer.dto.output.ApiResponseDto;
import com.doka.customer.exception.DokaException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
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

        // TODO: send log to rabbit queue
//        new SLCLogger(GlobalExceptionHandler.class.getSimpleName(), "handleValidationErrors")
//                .apiResponse(apiResponse)
//                .message(message)
//                .statusCode(apiResponse.getKod())
//                .sendErrorLog(exception);

        return ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Kod tarafından fırlatılan DokaException'lar için hata mesajı döner.
     */
    @ExceptionHandler(DokaException.class)
    public ResponseEntity<ApiResponseDto> handleDokaExceptions(DokaException exception) {
        ApiResponseDto apiResponse = new ApiResponseDto(exception.getMessage());

        // TODO: send log to rabbit queue
//        new SLCLogger(GlobalExceptionHandler.class.getSimpleName(), "handleApiExceptions")
//                .apiResponse(apiResponse)
//                .statusCode(apiResponse.getKod())
//                .message(apiResponse.getMesaj())
//                .sendErrorLog(exception);

        return ResponseEntity
                .status(exception.getErrorCode())
                .body(apiResponse);
    }

    /**
     * Geri kalan tüm exception'ları yakalar.
     */
    @ExceptionHandler
    public ResponseEntity<ApiResponseDto> handleAllOtherExceptions(Exception exception) {
        exception.printStackTrace();

        ApiResponseDto apiResponse = new ApiResponseDto(exception.getMessage());

        // TODO: send log to rabbit queue
//        new SLCLogger(GlobalExceptionHandler.class.getSimpleName(), "handleAllOtherExceptions")
//                .apiResponse(apiResponse)
//                .statusCode(apiResponse.getKod())
//                .message(apiResponse.getMesaj())
//                .sendErrorLog(exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiResponse);
    }

}
