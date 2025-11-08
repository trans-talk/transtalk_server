package com.wootech.transtalk.config.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.wootech.transtalk.config.util.JwtUtil.REFRESH_TOKEN_TIME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtil {

    /**
     * HttpOnly, Secure 옵션을 사용하여 Refresh Token을 쿠키로 저장
     */
    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(REFRESH_TOKEN_TIME);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}