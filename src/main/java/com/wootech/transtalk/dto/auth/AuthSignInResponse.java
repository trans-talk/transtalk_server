package com.wootech.transtalk.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignInResponse {
    private Long id;
    private String email;
    private String name;
    private String picture;
    private TokenResponse response;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }
}
