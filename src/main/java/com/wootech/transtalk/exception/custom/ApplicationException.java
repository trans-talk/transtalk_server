package com.wootech.transtalk.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;

    public ApplicationException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}