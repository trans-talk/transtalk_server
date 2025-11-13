package com.wootech.transtalk.dto.chatroom;

import com.wootech.transtalk.enums.TranslateLanguage;
import jakarta.validation.constraints.Email;

public record CreateChatRoomRequest(TranslateLanguage language, @Email(message = "이메일 형식이 잘못되었습니다.") String recipientEmail) {
}
