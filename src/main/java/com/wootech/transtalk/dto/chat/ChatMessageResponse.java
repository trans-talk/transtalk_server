package com.wootech.transtalk.dto.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import java.time.Instant;
import java.time.LocalDateTime;

public record ChatMessageResponse(Long chatId, String originalMessage, String translatedMessage,
                                  String senderEmail, Instant sendAt, boolean isRead, TranslationStatus status) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                Long.valueOf(message.getId()),
                message.getOriginalContent(),
                message.getTranslatedContent(),
                message.getSenderEmail(),
                message.getCreatedAt(),
                message.isRead(),
                message.getTranslationStatus()
        );
    }

}
