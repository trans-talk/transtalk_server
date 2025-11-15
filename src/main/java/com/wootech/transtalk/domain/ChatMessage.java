package com.wootech.transtalk.domain;

import com.wootech.transtalk.enums.TranslationStatus;
import java.time.LocalDateTime;

public class ChatMessage {
    private String id;
    private String originalContent;
    private String translatedContent;
    private Long chatRoomId;
    private Long senderId;
    private boolean read;
    private LocalDateTime createdAt;
    private TranslationStatus translationStatus;

    public ChatMessage(String id, String originalContent, String translatedContent, Long chatRoomId, Long senderId,
                       boolean read, LocalDateTime createdAt, TranslationStatus translationStatus) {
        this.id = id;
        this.originalContent = originalContent;
        this.translatedContent = translatedContent;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.read = read;
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

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public TranslationStatus getTranslationStatus() {
        return translationStatus;
    }
}
