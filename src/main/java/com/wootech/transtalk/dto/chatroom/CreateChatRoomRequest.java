package com.wootech.transtalk.dto.chatroom;

import com.wootech.transtalk.enums.TranslateLanguage;

public record CreateChatRoomRequest(TranslateLanguage language, String recipientEmail) {
}
