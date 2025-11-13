package com.wootech.transtalk.exception;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.exception.custom.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.wootech.transtalk.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ApiResponse.error(message, HttpStatus.BAD_REQUEST.name());
    }

    @ExceptionHandler(ApplicationException.class)
    public ApiResponse<String> handleAppException(ApplicationException e) {
        log.info("[GlobalExceptionHandler] " + e.getClass().getName() + ":" + e.getMessage());
        return ApiResponse.error(e.getMessage(), e.getHttpStatusCode().toString());
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