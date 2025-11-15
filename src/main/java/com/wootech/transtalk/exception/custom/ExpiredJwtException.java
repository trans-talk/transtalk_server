package com.wootech.transtalk.exception.custom;

import org.springframework.http.HttpStatusCode;

public class ExpiredJwtException extends ApplicationException {
    public ExpiredJwtException(String message, HttpStatusCode httpStatusCode) {
        super(message, httpStatusCode);
    }
}
