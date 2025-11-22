package com.wootech.transtalk.config;

import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.service.auth.BlackListService;
import com.wootech.transtalk.service.auth.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.wootech.transtalk.config.util.CookieUtil.deleteRefreshTokenCookie;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppLogOutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final BlackListService blackListService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = extractAccessToken(request);

        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            String jti = jwtUtil.extractJti(accessToken);
            Date exp = jwtUtil.extractExpiration(accessToken);
            long ttlSeconds = Math.max(0, (exp.getTime() - System.currentTimeMillis()) / 1000);
            if (jti != null && ttlSeconds > 0) {
                blackListService.add(jti, ttlSeconds);
                log.info("[AppLogOutHandler] Access Token JTI Blacklisted JTI={} ttl={}s", jti, ttlSeconds);
            }

            // access token 만료시
            String refreshToken = extractCookieValue(request);
            if (refreshToken != null) {
                try {
                    Long userId = Long.parseLong(jwtUtil.extractUserId(refreshToken));
                    if (refreshTokenService.hasRefreshToken(userId, refreshToken)) {
                        refreshTokenService.deleteRefreshToken(userId, refreshToken);
                    }
                } catch (Exception e) {
                    log.warn("[AppLogOutHandler] Failed to Delete Refresh Token: {}", e.getMessage());
                }
            }

            deleteRefreshTokenCookie(response);
            log.info("[LogOutHandler] Refresh Token Set Null");
        }
    }


    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String extractCookieValue(HttpServletRequest request) {
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
        return refreshToken;
    }
}
