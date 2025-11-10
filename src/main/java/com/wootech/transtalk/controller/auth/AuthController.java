package com.wootech.transtalk.controller.auth;

import com.wootech.transtalk.config.jwt.RefreshToken;
import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthSignInResponse;
import com.wootech.transtalk.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.wootech.transtalk.config.util.CookieUtil.addRefreshTokenCookie;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // 인가 코드 받기
    @GetMapping
    public ApiResponse<URI> createAuthorizationUrl() {
        return ApiResponse.success(authService.createRequest(), "인증 코드 url 요청에 성공했습니다.");
    }

    // 회원가입 및 로그인
    @GetMapping("/token")
    public ApiResponse<AuthSignInResponse> signIn(
            @RequestParam String code,
            HttpServletResponse httpServletResponse
    ) {
        AuthSignInResponse authSignInResponse = authService.googleLogin(code);
        addRefreshTokenCookie(httpServletResponse, authSignInResponse.getTokenresponse().getRefreshToken());
        return ApiResponse.success(authSignInResponse, "인증 요청에 성공했습니다.");
    }

    // 토큰 재발급
    @GetMapping("/refresh")
    public ApiResponse<AuthSignInResponse.TokenResponse> refresh(
            @RequestParam Long userId,
            @RefreshToken String refreshToken,
            HttpServletResponse httpServletResponse
    ) {
        AuthSignInResponse.TokenResponse tokenResponse = authService.refreshAccessToken(userId, refreshToken);
        addRefreshTokenCookie(httpServletResponse, tokenResponse.getRefreshToken());
        return ApiResponse.success(tokenResponse, "토큰 재발급에 성공했습니다.");
    }
}