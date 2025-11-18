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
    @Value("${REDIS_URL}")
    private String uri;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 단일 노드 Redis
        config.useSingleServer()
                .setAddress(uri)
                .setPassword(password)
                .setTimeout(3000);

        return Redisson.create(config);
    }
}