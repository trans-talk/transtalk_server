package com.wootech.transtalk.exception.custom;

import org.springframework.http.HttpStatusCode;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String message, HttpStatusCode statusCode) {
        super(message, statusCode);
    }
}
