package com.wootech.transtalk.controller.chatroom;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.dto.chatroom.ChatRoomListResponse;
import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatRooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping
    public ApiResponse<ChatRoomListResponse> findChatRooms(@AuthenticationPrincipal AuthUser authUser,
                                                           @PageableDefault(size = 40) Pageable pageable) {
        Page<ChatRoomResponse> pages = chatRoomService.findChatRoomsByUserId(authUser.getUserId(),
                pageable);
        ChatRoomListResponse response = new ChatRoomListResponse(pages.getContent(), pages.getNumberOfElements());
        return ApiResponse.success(response);
    }

    @PostMapping
    public ApiResponse<Long> saveChatRoom(@AuthenticationPrincipal AuthUser authUser,
                                                      @RequestParam String language,
                                                      @RequestParam String recipientEmail
    ) {
        return ApiResponse.success(chatRoomService.save(language, authUser.getEmail(), recipientEmail));
    }
}
