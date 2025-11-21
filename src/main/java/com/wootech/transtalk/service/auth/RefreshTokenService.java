package com.wootech.transtalk.service.auth;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedissonClient redissonClient;

    // refreshToken 저장
    public void saveRefreshToken(Long userId, String refreshToken) {
        RBucket<String> bucket = redissonClient.getBucket("refresh:" + userId + refreshToken);
        bucket.set(refreshToken, Duration.ofDays(7)); // 만료 시간 7일
    }

    // refreshToken 조회
    public String getRefreshToken(Long userId, String refreshToken) {
        RBucket<String> bucket = redissonClient.getBucket("refresh:" + userId + refreshToken);
        return bucket.get();
    }

    // refreshToken 값 존재 여부
    public boolean hasRefreshToken(Long userId, String refreshToken) {
        RBucket<String> bucket = redissonClient.getBucket("refresh:" + userId + refreshToken);
        return bucket.isExists();
    }

    // 로그아웃 기능 만든다면 추가
    // refreshToken 삭제 - 로그아웃
    public void deleteRefreshToken(Long userId, String refreshToken) {
        redissonClient.getBucket("refresh:" + userId + refreshToken).delete();
    }

    // 모든 토큰 제거
    public void deleteAllTokensByUserId(Long userId) {
        redissonClient.getKeys().deleteByPattern("refresh:" + userId + ":*");
    }
}