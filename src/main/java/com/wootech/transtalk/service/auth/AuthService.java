package com.wootech.transtalk.service.auth;

import com.wootech.transtalk.client.GoogleClient;
import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.dto.auth.AuthSignInResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.dto.auth.GoogleProfileResponse;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.event.user.UserWithdrawnEvent;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.user.UserRepository;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

import static com.wootech.transtalk.exception.ErrorMessages.USER_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final GoogleClient googleClient;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

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

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

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


    // 재발급
    public AuthSignInResponse.TokenResponse refreshAccessToken(String refreshToken) {

        jwtUtil.validateToken(refreshToken);

        Long userId = Long.parseLong(jwtUtil.extractUserId(refreshToken));
        log.info("[AuthService] Refresh Token Reissue Requested With User ID={}", userId);

        String storedToken = refreshTokenService.getRefreshToken(userId, refreshToken);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            log.warn("[UserService] Invalid Refresh Token={}", refreshToken);
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        // 소프트 딜리트된 user -> Optional.empty() or exception
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[UserService]Refresh Token Reissue Failed: User Doesn't Exists With ID={}", userId);
                    return new NotFoundException("User Not Found", HttpStatusCode.valueOf(404));
                });

        String newAccessToken = jwtUtil.createAccessToken(foundUser.getId(), foundUser.getEmail(), foundUser.getName(), foundUser.getUserRole());
        log.info("Access Token 재발급 성공: 사용자 ID = {}", userId);

        return AuthSignInResponse.TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(storedToken)
                .build();
    }

    // 회원탈퇴
    @Transactional
    public void withdrawUser(AuthUser authUser, String accessToken) {
        Long userId = authUser.getUserId();
        log.info("[UserService] Received User ID={}", userId);

        // 제거되지 않은 사용자만 조회
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404)));

        refreshTokenService.deleteAllTokensByUserId(userId);
        log.info("[AuthService] Deleted Refresh Tokens In Redis With User ID={}", userId);

        userRepository.deleteById(userId);
        log.info("[AuthService] Softly Deleted User ID={}", userId);

        googleClient.revokeToken(accessToken);

        publisher.publishEvent(new UserWithdrawnEvent(userId));
        log.info("[AuthService] UserWithdrawnEvent Published: User ID={}", userId);

        log.info("[AuthService] User Withdrawal Completed: User ID={}", userId);
    }
}