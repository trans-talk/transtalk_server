package com.wootech.transtalk.service.auth;

import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.client.GoogleClient;
import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.dto.auth.AuthSignInResponse;
import com.wootech.transtalk.dto.auth.GoogleProfileResponse;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public URI createRequest() {
        return googleClient.buildAuthorizeApiUri();
    }

    public AuthSignInResponse googleLogin(String code) {
        // 인가 토큰 받기
        String authorizationToken = googleClient.requestToken(code);

        // 사용자 정보 조회
        GoogleProfileResponse googleProfileResponse = googleClient.requestProfile(authorizationToken);

        // 사용자 정보 프로젝트에 저장 또는 있을 경우 반환
        User user = findByEmailOrGet(
                googleProfileResponse.getEmail(),
                googleProfileResponse.getName(),
                UserRole.ROLE_USER,
                googleProfileResponse.getPicture()
        );

        // 토큰 발급
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getUserRole());
        String refreshToken = jwtUtil.createRefreshToken();

        refreshTokenService.saveRefreshToken(refreshToken);

        return AuthSignInResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .picture(user.getPicture())
                .response(AuthSignInResponse.TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }

    // 회원가입
    private User findByEmailOrGet(String email, String name, UserRole userRole, String picture) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .userRole(userRole)
                                .picture(picture)
                                .build()
                ));
    }


    // refresh token 재발급
    public AuthSignInResponse.TokenResponse refreshAccessToken(Long userId, String refreshToken) {
        String storedToken = refreshTokenService.getRefreshToken(refreshToken);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[UserService] User Not Found: Received ID={}", userId);
                    return new NotFoundException("User Not Found");
                });

        String accessToken = jwtUtil.createAccessToken(foundUser.getId(), foundUser.getEmail(), foundUser.getName(), foundUser.getUserRole());

        return AuthSignInResponse.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}