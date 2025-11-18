package com.wootech.transtalk.event;

import lombok.Getter;

@Getter
public class MessageNotificationEvent extends Event {
    private final Long chatRoomId;
    private final Long currentUserId;

    public MessageNotificationEvent(Long chatRoomId,Long currentUserId) {
        super();
        this.chatRoomId = chatRoomId;
        this.currentUserId = currentUserId;
    }
}
