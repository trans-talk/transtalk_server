package com.wootech.transtalk.controller.chatroom;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.dto.chatroom.ChatRoomListResponse;
import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.dto.chatroom.CreateChatRoomRequest;
import com.wootech.transtalk.dto.chatroom.CreateChatRoomResponse;
import jakarta.validation.Valid;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
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
                                                           @PageableDefault(size = 20) Pageable pageable,
                                                           @RequestParam(required = false) String name
                                                           ) {
        Page<ChatRoomResponse> pages = chatRoomService.findChatRoomsByUserId(
                authUser.getUserId(),
                name,
                pageable);
        ChatRoomListResponse response = ChatRoomListResponse.from(pages);
        return ApiResponse.success(response);
    }

    @PostMapping
    public ApiResponse<CreateChatRoomResponse> createChatRoom(@AuthenticationPrincipal AuthUser authUser,
                                                              @Valid @RequestBody CreateChatRoomRequest request) {
        CreateChatRoomResponse response = chatRoomService.save(request.language(), authUser.getEmail(),
                request.recipientEmail());
        return ApiResponse.success(response);
    }
}
