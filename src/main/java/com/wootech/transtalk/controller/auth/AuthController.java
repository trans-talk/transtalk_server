package com.wootech.transtalk.controller.auth;

import com.wootech.transtalk.config.jwt.RefreshToken;
import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthSignInResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.service.auth.AuthService;
import com.wootech.transtalk.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

import static com.wootech.transtalk.config.util.CookieUtil.addRefreshTokenCookie;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

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
        authSignInResponse.getTokenresponse().removeRefreshToken();
        return ApiResponse.success(authSignInResponse, "인증 요청에 성공했습니다.");
    }

    // 토큰 재발급
    @GetMapping("/refresh")
    public ApiResponse<AuthSignInResponse.TokenResponse> refresh(
            @RefreshToken String refreshToken,
            HttpServletResponse httpServletResponse
    ) {
        AuthSignInResponse.TokenResponse tokenResponse = authService.refreshAccessToken(refreshToken);
        addRefreshTokenCookie(httpServletResponse, tokenResponse.getRefreshToken());
        tokenResponse.removeRefreshToken();
        return ApiResponse.success(tokenResponse, "토큰 재발급에 성공했습니다.");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<Object> logOut() {
        return ApiResponse.builder()
                .success(true)
                .message("로그아웃에 성공했습니다.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    //회원탈퇴
    @DeleteMapping("/withdraw")
    public ApiResponse<Object> withdrawUser(@AuthenticationPrincipal AuthUser authUser) {
        userService.withdrawUser(authUser);
        return ApiResponse.builder()
                .success(true)
                .message("회원탈퇴에 성공했습니다.")
                .timestamp(LocalDateTime.now())
                .build();
    }
}