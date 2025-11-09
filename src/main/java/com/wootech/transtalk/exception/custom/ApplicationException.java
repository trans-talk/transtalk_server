package com.wootech.transtalk.exception.custom;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
}
