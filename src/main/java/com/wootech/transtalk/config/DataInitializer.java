package com.wootech.transtalk.config;

import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.repository.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        //기본 유저 생성
        for (int i = 1; i <= 10; i++) {
            userRepository.save(User.builder()
                    .name("test" + i)
                    .picture("image" + i)
                    .email("test" + i + "@email.com")
                    .userRole(UserRole.ROLE_USER)
                    .build());
        }
    }
}
