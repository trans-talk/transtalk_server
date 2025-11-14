package com.wootech.transtalk.config.jwt;

import com.wootech.transtalk.config.util.JwtUtil;
import com.wootech.transtalk.event.Events;
import com.wootech.transtalk.event.ExitToChatRoomEvent;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null || !jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                throw new IllegalArgumentException("Invalid JWT Token");
            }

            String userEmail = jwtUtil.getEmail(token.replace("Bearer ", ""));
            accessor.setUser(new UsernamePasswordAuthenticationToken(userEmail, null, List.of()));
        } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
            exitToChatRoom(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            inToChatRoom(accessor);
        }

        return message;
    }

    private void inToChatRoom(StompHeaderAccessor accessor) {
        String subscriptionId = accessor.getSubscriptionId();
        String destination = accessor.getDestination();

        accessor.getSessionAttributes().put("sub-" + subscriptionId, destination);
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
