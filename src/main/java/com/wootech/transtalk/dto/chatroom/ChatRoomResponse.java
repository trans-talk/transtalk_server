package com.wootech.transtalk.dto.chatroom;

import lombok.Builder;
import java.time.Instant;


@Builder
public record ChatRoomResponse(Long chatroomId, String recipientPicture, String recipientName, String selectedLanguage,
                               String originalRecentMessage, String translatedRecentMessage,
                               Instant recentMessageTime, long unreadMessageCount) {
}
