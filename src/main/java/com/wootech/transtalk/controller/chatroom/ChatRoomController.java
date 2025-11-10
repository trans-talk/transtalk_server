package com.wootech.transtalk.controller.chatroom;

import com.wootech.transtalk.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

}
