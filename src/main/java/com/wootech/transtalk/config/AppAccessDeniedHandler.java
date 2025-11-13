package com.wootech.transtalk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootech.transtalk.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.wootech.transtalk.exception.ErrorMessages.FORBIDDEN_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("[AuthenticationEntryPoint] {}", accessDeniedException.getClass().getName());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> body = ApiResponse.error(FORBIDDEN_ERROR, HttpStatus.FORBIDDEN.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
