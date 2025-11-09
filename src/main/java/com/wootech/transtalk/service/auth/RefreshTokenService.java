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
    public void saveRefreshToken(String refreshToken) {
        RBucket<String> bucket = redissonClient.getBucket("refresh:" + refreshToken);
        bucket.set(refreshToken, Duration.ofDays(7)); // 만료 시간 7일
    }

    // refreshToken 조회
    public String getRefreshToken(String refreshToken) {
        RBucket<String> bucket = redissonClient.getBucket("refresh:" + refreshToken);
        return bucket.get();
    }

    // 로그아웃 기능 만든다면 추가
    // refreshToken 삭제 - 로그아웃
    public void deleteRefreshToken(String refreshToken) {
        redissonClient.getBucket("refresh:" + refreshToken).delete();
    }
}