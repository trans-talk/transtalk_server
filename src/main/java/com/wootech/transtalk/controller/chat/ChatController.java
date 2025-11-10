package com.wootech.transtalk.controller.chat;

import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat.{chatRoomId}")
    @SendTo("/topic/chat.{chatRoomId}")
    public ChatMessageResponse sendMessage(@DestinationVariable Long chatRoomId,
                                           ChatMessageRequest request,
                                           @AuthenticationPrincipal AuthUser authUser) {
        //TODO Change to pass using a DTO.
        return chatService.save(request, chatRoomId, authUser.getEmail());
    }
}
