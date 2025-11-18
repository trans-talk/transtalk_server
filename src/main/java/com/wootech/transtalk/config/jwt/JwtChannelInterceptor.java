package com.wootech.transtalk.config.jwt;

import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.exception.custom.UnauthorizedException;
import com.wootech.transtalk.event.Events;
import com.wootech.transtalk.event.ExitToChatRoomEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wootech.transtalk.exception.ErrorMessages.JWT_DOES_NOT_EXIST_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

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
        accessor.setUser(new UsernamePasswordAuthenticationToken(userEmail, null, List.of(UserRole.ROLE_USER)));

        log.info("[JwtChannelInterceptor] CONNECT - JWT Token validated for user={}", userEmail);
    }
    private String extractAccessToken(StompHeaderAccessor accessor) {
        String accessToken = accessor.getFirstNativeHeader("Authorization");
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

        Events.raise(new ExitToChatRoomEvent(userEmail,Long.valueOf(roomId)));
    }

}
