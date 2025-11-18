package com.wootech.transtalk.service.auth;

import com.wootech.transtalk.client.GoogleClient;
import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.dto.auth.AuthSignInResponse;
import com.wootech.transtalk.dto.auth.GoogleProfileResponse;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final GoogleClient googleClient;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public URI createRequest() {
        return googleClient.buildAuthorizeApiUri();
    }

    public AuthSignInResponse googleLogin(String code) {
        // 인가 토큰 받기
        String authorizationToken = googleClient.requestToken(code);

        // 사용자 정보 조회
        GoogleProfileResponse googleProfileResponse = googleClient.requestProfile(authorizationToken);

        // 사용자 정보 프로젝트에 저장 또는 있을 경우 반환
        User user = userService.findByEmailOrGet(
                googleProfileResponse.getEmail(),
                googleProfileResponse.getName(),
                UserRole.ROLE_USER,
                googleProfileResponse.getPicture()
        );

        // 토큰 발급
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getUserRole());
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getId()));

        refreshTokenService.saveRefreshToken(refreshToken);

        return AuthSignInResponse.builder()
                .userResponse(AuthSignInResponse.UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .picture(user.getPicture())
                        .build())
                .tokenresponse(AuthSignInResponse.TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }


    // refresh token 재발급
    public AuthSignInResponse.TokenResponse refreshAccessToken(String refreshToken) {
        String storedToken = refreshTokenService.getRefreshToken(refreshToken);

        jwtUtil.validateToken(storedToken);

        Long userId = Long.parseLong(jwtUtil.extractUserId(refreshToken));
        User foundUser = userService.getUserById(userId);

        String accessToken = jwtUtil.createAccessToken(foundUser.getId(), foundUser.getEmail(), foundUser.getName(), foundUser.getUserRole());

        return AuthSignInResponse.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(storedToken)
                .build();
    }
}