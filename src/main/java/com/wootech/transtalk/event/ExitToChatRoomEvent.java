package com.wootech.transtalk.event;

import lombok.Getter;

@Getter
public class ExitToChatRoomEvent extends Event{
    private final String userEamil;
    private final Long chatRoomId;

    public ExitToChatRoomEvent(String userEamil, Long chatRoomId) {
        super();
        this.userEamil = userEamil;
        this.chatRoomId = chatRoomId;
    }
}
