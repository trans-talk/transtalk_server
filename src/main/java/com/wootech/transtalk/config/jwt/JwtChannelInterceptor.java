package com.wootech.transtalk.config.jwt;

import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.exception.custom.ApplicationException;
import com.wootech.transtalk.exception.custom.UnauthorizedException;
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

import java.rmi.ServerException;
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
        if (command == StompCommand.CONNECT || command == StompCommand.SUBSCRIBE) {

            String token = accessor.getFirstNativeHeader("Authorization");

            if (token == null) {
                log.error("[JwtChannelInterceptor] " + JWT_DOES_NOT_EXIST_ERROR);
                throw new UnauthorizedException(JWT_DOES_NOT_EXIST_ERROR, HttpStatusCode.valueOf(401));
            }
            String userEmail;
            try {
                String accessToken = jwtUtil.substringToken(token);
                jwtUtil.validateToken(accessToken);
                userEmail = jwtUtil.getEmail(accessToken);
            } catch (RuntimeException e) {
                log.error("[JwtChannelInterceptor] " + e.getClass());
                throw new UnauthorizedException(e.getMessage(), HttpStatusCode.valueOf(401));
            } catch (ServerException e) {
                throw new ApplicationException(e.getMessage(), HttpStatusCode.valueOf(500));
            }
            accessor.setUser(new UsernamePasswordAuthenticationToken(userEmail, null, List.of(UserRole.ROLE_USER)));
        }
        return message;
    }

}
