package com.wootech.transtalk.controller.chat;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.entity.ChatContent;
import com.wootech.transtalk.service.chat.ChatContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatMongoController {

    private final ChatContentService chatContentService;

    @PostMapping
    public ApiResponse<ChatContent> save(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ChatMessageRequest request,
            @RequestParam Long chatRoomId
            ) {
        return ApiResponse.success(chatContentService.saveChat(authUser, request, chatRoomId));
    }
}
