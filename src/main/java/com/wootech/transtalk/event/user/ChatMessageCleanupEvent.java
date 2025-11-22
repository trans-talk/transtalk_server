package com.wootech.transtalk.event.user;

import com.wootech.transtalk.event.Event;
import lombok.Getter;

@Getter
public class ChatMessageCleanupEvent extends Event {
    private final Long chatRoomId;

    public ChatMessageCleanupEvent(Long chatRoomId) {
        super();
        this.chatRoomId = chatRoomId;
    }
}