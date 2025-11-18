package com.wootech.transtalk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.exception.custom.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.wootech.transtalk.exception.ErrorMessages.EXPIRED_JWT_TOKEN_ERROR;
import static com.wootech.transtalk.exception.ErrorMessages.UNAUTHORIZED_ERROR;
@Slf4j
@Component
@RequiredArgsConstructor
public class AppAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("[AuthenticationEntryPoint] {}", authException.getClass().getName());

        Throwable exception = (Throwable) request.getAttribute("exception");

        if (exception instanceof ExpiredJwtException) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType("application/json;charset=UTF-8");
            ApiResponse<Void> body = ApiResponse.error(EXPIRED_JWT_TOKEN_ERROR, HttpStatus.NOT_ACCEPTABLE.name());
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> body = ApiResponse.error(UNAUTHORIZED_ERROR, HttpStatus.UNAUTHORIZED.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
