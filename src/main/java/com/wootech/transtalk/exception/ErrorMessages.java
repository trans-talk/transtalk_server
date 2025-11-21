package com.wootech.transtalk.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {

    public static final String INTERNAL_SERVER_ERROR = "예기치 못한 서버 오류가 발생했습니다.";

    // UserRole
    public static final String INVALID_USER_ROLE_ERROR = "유효하지 않은 UserRole입니다.";

    // refresh token
    public static final String REFRESH_TOKEN_FORMATION_ERROR = "Refresh Token이 String 형태가 아닙니다.";
    public static final String REFRESH_TOKEN_DOES_NOT_EXISTS_ERROR = "Refresh Token값이 존재하지 않습니다.";
    public static final String INVALID_REFRESH_TOKEN_ERROR = "유효하지 않은 Refresh Token입니다.";
    public static final String EXPIRED_REFRESH_TOKEN_ERROR = "만료된 Refresh Token 입니다.";

    // jwt
    public static final String INVALID_JWT_SIGNATURE_ERROR = "유효하지 않는 JWT 서명 입니다.";
    public static final String EXPIRED_JWT_TOKEN_ERROR = "만료된 JWT 토큰 입니다.";
    public static final String UNSUPPORTED_JWT_TOKEN_ERROR = "지원하지 않는 JWT 토큰 입니다.";
    public static final String MALFORMED_JWT_TOKEN_ERROR = "잘못된 형식의 JWT 토큰 입니다.";
    public static final String SECURITY_VALIDATION_ERROR = "서명 검증에 실패하였습니다.";
    public static final String JWT_DOES_NOT_EXIST_ERROR = "JWT 토큰이 존재하지 않습니다.";

    public static final String UNAUTHORIZED_ERROR = "인증이 필요합니다.";
    public static final String FORBIDDEN_ERROR = "접근 권한이 없습니다.";
    public static final String WITHDRAWN_USER_ERROR = "탈퇴한 사용자입니다.";

    // google
    public static final String ACCESS_TOKEN_DOES_NOT_EXISTS_ERROR = "Access Token값이 존재하지 않습니다.";

    // user
    public static final String USER_NOT_FOUND_ERROR = "사용자 정보 조회에 실패했습니다.";

    // ChatRoom
    public static final String CHAT_ROOM_NOT_FOUND_ERROR = "채팅방 조회에 실패했습니다.";
    public static final String PARTICIPANT_NOT_FOUND_ERROR = "채팅방 참여자 정보 조회에 실패했습니다.";

    // TranslateLanguage
    public static final String NOT_SUPPORTED_LANGUAGE_CODE_ERROR = "지원하지 않는 언어 코드입니다.";
    public static final String DUPLICATE_TRANSLATION_ERROR = "이미 번역이 완료된 메세지 입니다.";

    //Chat
    public static final String CHAT_NOT_FOUND_ERROR = "채팅 조회에 실패했습니다.";
}
