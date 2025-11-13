package com.wootech.transtalk.config.util;

import com.wootech.transtalk.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.rmi.ServerException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static com.wootech.transtalk.exception.ErrorMessages.*;

@Slf4j
@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_TIME = 10 * 60 * 1000L; // 10분
    public static final int REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60; // 1주일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private SecretKey key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(Long userId, String email, String name, UserRole userRole) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("email", email)
                        .claim("name", name)
                        .claim("userRole", userRole)
                        .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public String createRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public String substringToken(String tokenValue) throws ServerException {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new ServerException("JWT Not Found");
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 웹소켓을 위해 추가
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn(EXPIRED_JWT_TOKEN_ERROR + ": {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn(UNSUPPORTED_JWT_TOKEN_ERROR + ": {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn(MALFORMED_JWT_TOKEN_ERROR + ": {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn(SECURITY_VALIDATION_ERROR + ": {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn(INVALID_JWT_SIGNATURE_ERROR + ": {}", e.getMessage());
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR + ": {}", e.getMessage());
        }
        return false;
    }

    public String getEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }

    public Long getUserId(String token) {
        return Long.valueOf(extractClaims(token).getSubject());
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}