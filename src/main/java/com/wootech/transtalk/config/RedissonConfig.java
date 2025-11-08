package com.wootech.transtalk.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 단일 노드 Redis
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setPassword(password)
                .setTimeout(3000);

        return Redisson.create(config);
    }
}