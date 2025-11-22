package com.wootech.transtalk.config.jwt;

import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.event.Events;
import com.wootech.transtalk.event.ExitToChatRoomEvent;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.exception.custom.UnauthorizedException;
import com.wootech.transtalk.service.auth.BlackListService;
import com.wootech.transtalk.service.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wootech.transtalk.exception.ErrorMessages.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final BlackListService blackListService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            log.error("[JwtChannelInterceptor] " + JWT_DOES_NOT_EXIST_ERROR);
            throw new UnauthorizedException(JWT_DOES_NOT_EXIST_ERROR, HttpStatusCode.valueOf(401));
        }

        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        } else if (StompCommand.UNSUBSCRIBE.equals(command)) {
            exitToChatRoom(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            inToChatRoom(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String accessToken = extractAccessToken(accessor);
        validateAccessToken(accessToken);

        String userEmail = jwtUtil.getEmail(accessToken);
        // 탈퇴된 사용자라면 404 에러
        try {
            userDetailsService.loadUserByUsername(userEmail);
        } catch (NotFoundException e) {
            log.error(WITHDRAWN_USER_ERROR);
            throw new NotFoundException(WITHDRAWN_USER_ERROR, HttpStatusCode.valueOf(404));
        }
        accessor.setUser(new UsernamePasswordAuthenticationToken(userEmail, null, List.of(UserRole.ROLE_USER)));
        log.info("[JwtChannelInterceptor] CONNECT - JWT Token validated for user={}", userEmail);
    }

    private String extractAccessToken(StompHeaderAccessor accessor) {
        String accessToken = accessor.getFirstNativeHeader("Authorization");
        // 로그아웃 처리
        String jti = jwtUtil.extractJti(accessToken);
        if (blackListService.contains(jti)) {
            throw new MessagingException(LOGGED_OUT_USER_ERROR);
        }
        if (accessToken == null || accessToken.isBlank()) {
            log.error("[JwtChannelInterceptor] {}", JWT_DOES_NOT_EXIST_ERROR);
            throw new UnauthorizedException(JWT_DOES_NOT_EXIST_ERROR, HttpStatusCode.valueOf(401));
        }

        accessToken = accessToken.replaceAll("Bearer ", "");

        return accessToken.trim();
    }

    private void validateAccessToken(String accessToken) {
        try {
            if (!jwtUtil.validateToken(accessToken)) {
                throw new UnauthorizedException("Invalid JWT Token", HttpStatusCode.valueOf(401));
            }
        } catch (RuntimeException e) {
            log.error("[JwtChannelInterceptor] JWT validation failed: {}", e.getMessage());
            throw new UnauthorizedException(e.getMessage(), HttpStatusCode.valueOf(401));
        }
    }

    private void inToChatRoom(StompHeaderAccessor accessor) {
        String subscriptionId = accessor.getSubscriptionId();
        String destination = accessor.getDestination();
        accessor.getSessionAttributes().put("sub-" + subscriptionId, destination);

        handleConnect(accessor);
    }

    private void exitToChatRoom(StompHeaderAccessor accessor) {
        String subscriptionId = accessor.getSubscriptionId();

        String destination = (String) accessor.getSessionAttributes().get("sub-" + subscriptionId);
        if (!destination.startsWith("/topic/chat/")) {
            return;
        }

        String roomId = destination.replace("/topic/chat/", "");
        String userEmail = accessor.getUser().getName();

        Events.raise(new ExitToChatRoomEvent(userEmail, Long.valueOf(roomId)));
    }

}
