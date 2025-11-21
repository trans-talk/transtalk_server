package com.wootech.transtalk.config;

import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.service.auth.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppLogOutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 쿠키에서 refresh token 조회
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        Long userId = null;

        // access token 유효시
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser) {
            AuthUser authUser = (AuthUser) authentication.getPrincipal();
            userId = authUser.getUserId();
            log.info("[AppLogOutHandler] - Get Authentication From User ID={}", userId);
        }

        // access token 만료시
        if (userId == null && refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            try {
                userId = Long.parseLong(jwtUtil.extractUserId(refreshToken));
                log.info("[LogOutHandler] Log Out Requested: Extract User ID={} From Refresh Token", userId);
            } catch (Exception e) {
                log.warn("Extract Failed From Refresh Token={}", e.getMessage());
            }
        }

        if (userId != null && refreshToken != null) {
            if (refreshTokenService.hasRefreshToken(userId, refreshToken)) {
                refreshTokenService.deleteRefreshToken(userId, refreshToken);
                log.info("[LogOutHandler] Delete Refresh Token Succeed");
            } else {
                log.warn("[LogOutHandler] Delete Refresh Token Failed Refresh Token={}", refreshToken);
            }

            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            response.addCookie(refreshTokenCookie);
            log.info("[LogOutHandler] Refresh Token Set Null");
        }
    }
}
