package com.wootech.transtalk.enums;

import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.exception.custom.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

import static com.wootech.transtalk.enums.UserRole.Authority.USER;
import static com.wootech.transtalk.exception.ErrorMessages.INVALID_USER_ROLE_ERROR;

@Getter
@RequiredArgsConstructor
public enum UserRole implements GrantedAuthority {

    ROLE_USER(USER),
    ROLE_ADMIN(Authority.ADMIN);

    private final String userRole;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.getUserRole().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() ->  new NotFoundException(INVALID_USER_ROLE_ERROR, HttpStatusCode.valueOf(404)));
    }

    @Override
    public String getAuthority() {
        return userRole;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }

    public static boolean isUser(AuthUser authUser) {
        return authUser.getAuthorities().stream()
                .anyMatch(auth -> USER.equals(auth.getAuthority()));
    }
}