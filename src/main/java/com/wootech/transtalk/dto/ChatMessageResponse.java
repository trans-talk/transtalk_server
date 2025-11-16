package com.wootech.transtalk.dto;

import com.wootech.transtalk.domain.ChatMessage;
import java.time.Instant;
import com.wootech.transtalk.enums.TranslationStatus;
import java.time.LocalDateTime;

public record ChatMessageResponse(Long chatId, String originalMessage, String translatedMessage,
                                  String senderEmail, LocalDateTime sendAt, int unReadCount, TranslationStatus status) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                Long.valueOf(message.getId()),
                message.getOriginalContent(),
                message.getTranslatedContent(),
                message.getSenderEmail(),
                message.getCreatedAt(),
                message.getUnReadCount(),
                message.getTranslationStatus()
        );
    }

}
