package com.wootech.transtalk.event;

import com.wootech.transtalk.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExitToChatRoomEventHandler {
    private final ChatRoomService chatRoomService;

    @EventListener(ExitToChatRoomEvent.class)
    public void handle(ExitToChatRoomEvent event) {
        chatRoomService.updateLastReadChatMessage(event.getUserEamil(),event.getChatRoomId());
    }
}
