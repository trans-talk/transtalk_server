package com.wootech.transtalk.config;

import com.wootech.transtalk.config.jwt.RefreshToken;
import com.wootech.transtalk.exception.custom.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.wootech.transtalk.exception.ErrorMessages.*;


@RequiredArgsConstructor
public class RefreshArgumentResolver implements HandlerMethodArgumentResolver {

    private final RedissonClient redissonClient;

    @Override
    public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
        boolean hasRefreshTokenAnnotation = parameter.getParameterAnnotation(RefreshToken.class) != null;
        boolean isStringType = parameter.getParameterType().equals(String.class);

        if (hasRefreshTokenAnnotation && !isStringType) {
            throw new UnauthorizedException(REFRESH_TOKEN_FORMATION_ERROR);
        }
        return hasRefreshTokenAnnotation;
    }

    @Override
    public Object resolveArgument(org.springframework.core.MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String refreshToken = extractRefreshTokenFromCookies(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new UnauthorizedException(REFRESH_TOKEN_DOES_NOT_EXISTS_ERROR);
        }

        // Redis에서 refreshToken 검증
        RBucket<String> bucket = redissonClient.getBucket("refresh:" + refreshToken);
        String storedToken = bucket.get();

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new UnauthorizedException(INVALID_REFRESH_TOKEN_ERROR);
        }

        return refreshToken;
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}