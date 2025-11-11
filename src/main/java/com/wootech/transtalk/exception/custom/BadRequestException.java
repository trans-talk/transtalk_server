package com.wootech.transtalk.exception.custom;

public class BadRequestException extends ApplicationException{
    public BadRequestException(String message) {
        super(message);
    }
}
