package com.wootech.transtalk.event.user;

import com.wootech.transtalk.event.Event;
import lombok.Getter;

@Getter
public class UserWithdrawnEvent extends Event {
    private final Long userId;

    public UserWithdrawnEvent(Long userId) {
        super();
        this.userId = userId;
    }
}