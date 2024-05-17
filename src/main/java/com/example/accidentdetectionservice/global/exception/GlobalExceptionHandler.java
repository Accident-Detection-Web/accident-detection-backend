package com.example.accidentdetectionservice.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void ExHandler(Exception e) {
        log.error(e.toString()); // 로그에 예외 정보 출력
        e.printStackTrace(); // 콘솔에 예외 스택 트레이스를 출력

        String errorMessage = e.getMessage();
        log.error("Error message: " + errorMessage);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exc) {
        log.error("Illegal argument exception: " + exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.error("Max upload size exceeded: " + exc.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("파일 크기가 너무 큽니다! 최대 허용 크기는 15MB 입니다.");
    }


// Response Entity (body, status code)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<RestApiException> ExHandler(Exception exception) {
//        log.error(exception.toString());
//        exception.printStackTrace();
//
//        RestApiException restApiException = new RestApiException(
//            exception.getMessage(),
//            HttpStatus.BAD_REQUEST.value());
//
//        return new ResponseEntity<>(
//            restApiException,
//            HttpStatus.BAD_REQUEST
//        );
//    }
}
