package com.wootech.transtalk.dto.chat.mongo;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MongoChatMessageResponse {
    private String id;
    private String originalContent;
    private String translatedContent;
    private TranslationStatus translationStatus;
    private String senderEmail;
    private boolean isRead;
    private Instant sendAt;

    public static MongoChatMessageResponse from(ChatMessage message) {
        return new MongoChatMessageResponse(
                message.getId(),
                message.getOriginalContent(),
                message.getTranslatedContent(),
                message.getTranslationStatus(),
                message.getSenderEmail(),
                message.isRead(),
                message.getCreatedAt()
        );
    }
}
