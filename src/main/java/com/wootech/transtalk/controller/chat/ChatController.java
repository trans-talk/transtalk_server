package com.wootech.transtalk.controller.chat;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.chat.ChatMessageListResponse;
import com.wootech.transtalk.dto.chat.ChatMessageRequest;
import com.wootech.transtalk.dto.chat.ChatMessageResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.service.chat.ChatService;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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

    @GetMapping("/api/v1/chatRooms/{chatRoomId}/chats")
    public ApiResponse<ChatMessageListResponse> getChats(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long chatRoomId,
            @PageableDefault(size = 40) Pageable pageable
    ) {
        ChatMessageListResponse chats = chatService.getChats(authUser, chatRoomId, pageable);
        return ApiResponse.success(chats);
    }


}
