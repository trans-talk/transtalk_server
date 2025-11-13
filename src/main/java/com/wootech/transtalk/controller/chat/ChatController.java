package com.wootech.transtalk.controller.chat;

import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/topic/chat/{chatRoomId}")
    public ChatMessageResponse sendMessage(@DestinationVariable Long chatRoomId,
                                           String message,
                                           Principal principal) {
        //TODO Change to pass using a DTO.
        log.info("Received message='{}' from user='{}'", message, principal != null ? principal.getName() : "null");
        return chatService.save(message, chatRoomId, principal.getName());
    }
}
