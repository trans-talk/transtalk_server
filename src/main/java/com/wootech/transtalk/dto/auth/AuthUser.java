package com.wootech.transtalk.dto.auth;


import com.wootech.transtalk.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser implements UserDetails {

    private final Long userId;
    private final String email;
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;

    @Builder
    public AuthUser(Long userId, String email, String name, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }
}