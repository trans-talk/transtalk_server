package com.wootech.transtalk.exception.custom;

import org.springframework.http.HttpStatusCode;

public class BadRequestException extends ApplicationException{
    public BadRequestException(String message, HttpStatusCode statusCode) {
        super(message, statusCode);
    }
}
