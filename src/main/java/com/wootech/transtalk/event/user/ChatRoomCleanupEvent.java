package com.wootech.transtalk.event.user;

import com.wootech.transtalk.event.Event;
import lombok.Getter;

@Getter
public class ChatRoomCleanupEvent extends Event {
    private final Long chatRoomId;

    public ChatRoomCleanupEvent(Long chatRoomId) {
        super();
        this.chatRoomId = chatRoomId;
    }
}