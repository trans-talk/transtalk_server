package com.wootech.transtalk.service.auth;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final RedissonClient redissonClient;

    private static final String KEY_PREFIX = "bl:jti:";

    // 블랙리스트에 jti 등록 (TTL은 토큰 만료까지)
    public void add(String jti, long ttlSeconds) {
        String key = KEY_PREFIX + jti;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set("1", ttlSeconds, TimeUnit.SECONDS);
    }

    public boolean contains(String jti) {
        String key = KEY_PREFIX + jti;
        return redissonClient.getBucket(key).isExists();
    }
}
