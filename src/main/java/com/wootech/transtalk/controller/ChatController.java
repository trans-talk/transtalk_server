package com.wootech.transtalk.controller;

import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat.{chatRoomId}")
    @SendTo("/subscribe/chat.{chatRoomId}")
    public ChatMessageResponse sendMessage(@DestinationVariable Long chatRoomId, ChatMessageRequest request) {
        System.out.println(request.content());
        return null;
    }
}
