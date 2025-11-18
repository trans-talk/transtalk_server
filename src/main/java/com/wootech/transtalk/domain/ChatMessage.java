package com.wootech.transtalk.domain;

import static com.wootech.transtalk.exception.ErrorMessages.DUPLICATE_TRANSLATION_ERROR;

import com.wootech.transtalk.enums.TranslationStatus;
import com.wootech.transtalk.event.Events;
import com.wootech.transtalk.event.MessageNotificationEvent;
import com.wootech.transtalk.exception.custom.ConflictException;
import java.time.Instant;
import org.springframework.http.HttpStatusCode;

public class ChatMessage {
    private String id;
    private String originalContent;
    private String translatedContent;
    private Long chatRoomId;
    private Long senderId;
    private String senderEmail;
    private boolean isRead;
    private Instant createdAt;
    private TranslationStatus translationStatus;

    public ChatMessage(String id, String originalContent, String translatedContent, Long chatRoomId, Long senderId, String senderEmail,
                       boolean isRead, Instant createdAt, TranslationStatus translationStatus) {
        this.id = id;
        this.originalContent = originalContent;
        this.translatedContent = translatedContent;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.translationStatus = translationStatus;
    }

    public String getId() {
        return id;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public String getTranslatedContent() {
        return translatedContent;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public boolean isRead() {
        return isRead;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public TranslationStatus getTranslationStatus() {
        return translationStatus;
    }

    public void completeTranslate(String translatedContent) {
        completeTranslate();
        this.translatedContent = translatedContent;
        Events.raise(new MessageNotificationEvent(chatRoomId, senderId));
    }
    private void completeTranslate() {
        validDuplicateTranslation();
        this.translationStatus = TranslationStatus.COMPLETED;
    }
    private void validDuplicateTranslation() {
        if (this.translationStatus != TranslationStatus.PENDING) {
            throw new ConflictException(DUPLICATE_TRANSLATION_ERROR, HttpStatusCode.valueOf(409));
        }
    }
}
