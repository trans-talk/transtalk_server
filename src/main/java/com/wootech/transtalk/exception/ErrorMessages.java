package com.wootech.transtalk.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {

    public static final String INTERNAL_SERVER_ERROR = "서버 오류가 발생했습니다.";

    // refresh token
    public static final String REFRESH_TOKEN_FORMATION_ERROR = "Refresh Token이 String 형태가 아닙니다.";
    public static final String REFRESH_TOKEN_DOES_NOT_EXISTS_ERROR = "Refresh Token값이 존재하지 않습니다.";
    public static final String INVALID_REFRESH_TOKEN_ERROR = "유효하지 않은 Refresh Token입니다.";

    // jwt
    public static final String INVALID_JWT_SIGNATURE_ERROR = "유효하지 않는 JWT 서명 입니다.";
    public static final String EXPIRED_JWT_TOKEN_ERROR = "만료된 JWT token 입니다.";
    public static final String UNSUPPORTED_JWT_TOKEN_ERROR = "지원되지 않는 JWT 토큰 입니다.";

    // google
    public static final String ACCESS_TOKEN_DOES_NOT_EXISTS_ERROR = "Access Token값이 존재하지 않습니다.";

    // user
    public static final String USER_NOT_FOUND_ERROR = "사용자 정보 조회에 실패했습니다.";
}
