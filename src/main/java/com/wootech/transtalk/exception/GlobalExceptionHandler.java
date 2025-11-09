package com.wootech.transtalk.exception;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.exception.custom.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.wootech.transtalk.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<String>> handleAppException(ApplicationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException e) {
        log.error("[GlobalExceptionHandler] Runtime Error: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(ApiResponse.error(e.getMessage(), e.getClass().getName()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        log.error("[GlobalExceptionHandler] Server Error: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(ApiResponse.error(("Exception Unexpected: " + e.getMessage()), e.getClass().getName()));

    }
}