package com.wootech.transtalk.service.user;

import com.wootech.transtalk.dto.auth.AuthSignInResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.wootech.transtalk.exception.ErrorMessages.USER_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 사용자 프로필 조회
    public AuthSignInResponse.UserResponse getProfileById(AuthUser authUser) {
        User foundUser = getUserById(authUser.getUserId());

        return AuthSignInResponse.UserResponse.builder()
                .id(foundUser.getId())
                .email(foundUser.getEmail())
                .name(foundUser.getName())
                .picture(foundUser.getPicture())
                .build();
    }

    // 회원가입
    public User findByEmailOrGet(String email, String name, UserRole userRole, String picture) {
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

    // 사용자 조회
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[AuthService] Received User Id={}", id);
                    return new NotFoundException(USER_NOT_FOUND_ERROR);
                });
    }
}
