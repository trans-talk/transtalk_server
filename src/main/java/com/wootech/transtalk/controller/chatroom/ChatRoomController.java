package com.wootech.transtalk.controller.chatroom;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/api/vi/chatRooms")
    public ApiResponse<List<ChatRoomResponse>> findChatRooms(@AuthenticationPrincipal AuthUser authUser) {
        List<ChatRoomResponse> responses = chatRoomService.findChatRoomsByUserId(authUser.getUserId());
        return ApiResponse.success(responses);
    }
}
