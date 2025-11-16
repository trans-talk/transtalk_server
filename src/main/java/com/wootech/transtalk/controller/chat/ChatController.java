package com.wootech.transtalk.controller.chat;

import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.service.chat.ChatService;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/topic/chat/{chatRoomId}")
    public ChatMessageResponse sendMessage(@DestinationVariable Long chatRoomId,
                                           ChatMessageRequest chatMessageRequest,
                                           Principal principal)
    {
        log.info("Received message='{}' from user='{}'", chatMessageRequest.content(), principal != null ? principal.getName() : "null");
        return chatService.save(chatMessageRequest.content(), chatRoomId, principal.getName());
    }

    @GetMapping("/api/v1/chatrooms/{chatRoomId}/chats")
    public List<Chat> getChats(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "40") int size
    ) {
        return null;
//        return chatService.getChats(authUser, chatRoomId, size);
    }


}
