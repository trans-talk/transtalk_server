package com.wootech.transtalk.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {

    public static final String INTERNAL_SERVER_ERROR ="서버 오류가 발생했습니다.";

    public static final String REFRESH_TOKEN_FORMATION_ERROR = "Refresh Token이 String 형태가 아닙니다.";
    public static final String REFRESH_TOKEN_DOES_NOT_EXISTS_ERROR = "Refresh Token값이 존재하지 않습니다.";
    public static final String INVALID_REFRESH_TOKEN_ERROR = "유효하지 않은 Refresh Token입니다.";
}
