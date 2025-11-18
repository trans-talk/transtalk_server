package com.wootech.transtalk.controller.chat;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.dto.chat.ChatMessageListResponse;
import com.wootech.transtalk.dto.chat.ChatMessageRequest;
import com.wootech.transtalk.dto.chat.ChatMessageResponse;
import com.wootech.transtalk.dto.chat.mongo.MongoChatMessageListResponse;
import com.wootech.transtalk.dto.chat.mongo.MongoChatMessageResponse;
import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.service.chat.MongoChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MongoChatController {

    private final MongoChatService mongoChatService;

    @MessageMapping("/chat/{chatRoomId}/mongo")
    @SendTo("/topic/chat/{chatRoomId}/mongo")
    public MongoChatMessageResponse sendMessage(
            @DestinationVariable Long chatRoomId,
            ChatMessageRequest chatMessageRequest,
            Principal principal
    ) {
        log.info("Received message='{}' from user='{}'", chatMessageRequest.content(), principal != null ? principal.getName() : "null");
        return mongoChatService.saveChat(principal, chatMessageRequest, chatRoomId);

    }

    @GetMapping("/api/v1/chatRooms/{chatRoomId}/mongo")
    public ApiResponse<MongoChatMessageListResponse> getChats(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long chatRoomId,
            @PageableDefault(size = 40, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("Received Chat Room ID={}", chatRoomId);
        MongoChatMessageListResponse chats = mongoChatService.getChats(authUser, chatRoomId, pageable);
        return ApiResponse.success(chats);
    }
}
