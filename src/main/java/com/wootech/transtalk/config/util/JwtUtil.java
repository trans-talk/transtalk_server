package com.wootech.transtalk.config.util;

import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.exception.custom.UnauthorizedException;
import com.wootech.transtalk.exception.custom.ExpiredJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    private static final long ACCESS_TOKEN_TIME = 30 * 10 * 6 * 1000L; // 30분
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

    // 유저 id 값을 갖는 refresh token
    public String createRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject("refresh-token")
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String substringToken(String tokenValue) throws ServerException {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new UnauthorizedException(JWT_DOES_NOT_EXIST_ERROR, HttpStatusCode.valueOf(401));
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 웹소켓에서 사용 - access token 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error(EXPIRED_JWT_TOKEN_ERROR + ": {}", e.getMessage());
            throw new ExpiredJwtException(EXPIRED_JWT_TOKEN_ERROR, HttpStatus.valueOf(406));
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

    // 웹소켓에서 사용 - access token 에서 email 추출
    public String getEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }

    // refresh token 유효성 검증
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error(EXPIRED_REFRESH_TOKEN_ERROR + ": {}", e.getMessage());
            throw new ExpiredJwtException(EXPIRED_REFRESH_TOKEN_ERROR, HttpStatus.valueOf(406));
        } catch (Exception e) {
            return false;
        }
    }

    // refresh token 에서 userId 값 추출
    public String extractUserId(String refreshToken) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        return claims.get("userId", String.class);
    }
}