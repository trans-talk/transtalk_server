package com.wootech.transtalk.exception.custom;

import org.springframework.http.HttpStatusCode;

public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String message, HttpStatusCode statusCode) {
        super(message, statusCode);
    }
}
