package com.wootech.transtalk.dto.chatroom;

import java.time.Instant;

public record ChatRoomResponse(Long chatroomId, String recipientPicture, String recipientName, String selectedLanguage,
                               String originalRecentMessage, String translatedRecentMessage,
                               Instant recentMessageTime, int unreadMessageCount) {
}
