package com.wootech.transtalk.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.service.auth.BlackListService;
import com.wootech.transtalk.service.user.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.wootech.transtalk.exception.ErrorMessages.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final BlackListService blackListService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = jwtUtil.substringToken(authorizationHeader);

            String jti = jwtUtil.extractJti(jwt);
            if (jti != null && blackListService.contains(jti)) {
                // 블랙리스트에 있으면 즉시 인증 실패 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                ApiResponse<Void> body = ApiResponse.error(LOGGED_OUT_USER_ERROR, HttpStatus.UNAUTHORIZED.name());
                response.getWriter().write(objectMapper.writeValueAsString(body));
                return;
            }

            try {
                Claims claims = jwtUtil.extractClaims(jwt);
                String email = jwtUtil.getEmail(jwt);

                // 탈퇴된 사용자라면 404 에러
                userDetailsService.loadUserByUsername(email);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(claims);
                }

            }catch (NotFoundException e) {
                log.error(WITHDRAWN_USER_ERROR);
                throw new AuthenticationCredentialsNotFoundException(WITHDRAWN_USER_ERROR, e);
            } catch (SecurityException | MalformedJwtException e) {
                log.error(INVALID_JWT_SIGNATURE_ERROR, e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_JWT_SIGNATURE_ERROR);
                return;
            } catch (ExpiredJwtException e) {
                log.error(EXPIRED_JWT_TOKEN_ERROR, e);
                request.setAttribute("exception", e);
                throw new AuthenticationException(EXPIRED_JWT_TOKEN_ERROR) {};
            } catch (UnsupportedJwtException e) {
                log.error(UNSUPPORTED_JWT_TOKEN_ERROR, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, UNSUPPORTED_JWT_TOKEN_ERROR);
                return;
            } catch (Exception e) {
                log.error(INTERNAL_SERVER_ERROR, e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Claims claims) {
        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        String name = claims.get("name", String.class);
        UserRole userRole = UserRole.of(claims.get("userRole", String.class));

        AuthUser authUser = new AuthUser(userId, email, name, userRole);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}