package com.wootech.transtalk.exception.custom;

import org.springframework.http.HttpStatusCode;

public class GoogleApiException extends ApplicationException {
    public GoogleApiException(String message, HttpStatusCode statusCode) {
        super(message, statusCode);
    }
}
