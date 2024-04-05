package com.example.accidentdetectionservice.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiException> ExHandler(Exception exception) {
        log.error(exception.toString());
        exception.printStackTrace();
        RestApiException restApiException = new RestApiException(
            exception.getMessage(),
            HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
            restApiException,
            HttpStatus.BAD_REQUEST
        );
    }
}
