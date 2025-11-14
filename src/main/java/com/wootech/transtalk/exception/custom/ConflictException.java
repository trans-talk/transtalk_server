package com.wootech.transtalk.exception.custom;

import org.springframework.http.HttpStatusCode;

public class ConflictException extends ApplicationException{
    public ConflictException(String message, HttpStatusCode httpStatusCode) {
        super(message, httpStatusCode);
    }
}
