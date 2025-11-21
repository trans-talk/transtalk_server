package com.wootech.transtalk.config.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.wootech.transtalk.config.util.JwtUtil.REFRESH_TOKEN_TIME;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtil {

    // HttpOnly, Secure 옵션을 사용하여 Refresh Token을 쿠키로 저장
    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(REFRESH_TOKEN_TIME);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // sames site = none 값
        String cookieValue = String.format(
                "%s=%s; Max-Age=%d; Path=/; Secure; HttpOnly; SameSite=None",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge()
        );

        response.addHeader("Set-Cookie", cookieValue);
    }

    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        String cookieName = "refreshToken";

        String cookieValue = String.format(
                "%s=; Max-Age=0; Path=/; Secure; HttpOnly; SameSite=None",
                cookieName
        );

        response.addHeader("Set-Cookie", cookieValue);
        log.info("[CookieUtil] Refresh Token Cookie Deleted: {}", cookieName);
    }
}