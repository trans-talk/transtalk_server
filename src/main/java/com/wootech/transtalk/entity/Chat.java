package com.wootech.transtalk.entity;


import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String originalContent;
    @Column(nullable = true)
    private String translatedContent;
    @Column(nullable = false)
    private boolean isRead;
    @Column(nullable = false)
    private Long senderId;
    @Column(nullable = false)
    private Long chatRoomId;
    private TranslationStatus translationStatus;
    @Column(nullable = false)
    private String senderEmail;

    public Chat(String originalContent, Long senderId, String senderEmail, Long chatRoomId, boolean isRead) {
        this.originalContent = originalContent;
        this.isRead = isRead;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.chatRoomId = chatRoomId;
        this.translationStatus = TranslationStatus.PENDING;
    }

    public void applyDomain(ChatMessage chatMessage) {
        this.translatedContent = chatMessage.getTranslatedContent();
        this.translationStatus = chatMessage.getTranslationStatus();
    }


    public static Chat fromDomain(ChatMessage chatMessage) {
        Chat entity = new Chat(
                chatMessage.getOriginalContent(),
                chatMessage.getSenderId(),
                chatMessage.getSenderEmail(),
                chatMessage.getChatRoomId(),
                chatMessage.isRead()
                );

        return entity;
    }

    public ChatMessage toDomain() {
        return new ChatMessage(
                String.valueOf(this.id),
                this.getOriginalContent(),
                this.getTranslatedContent(),
                this.chatRoomId,
                this.senderId,
                this.senderEmail,
                this.isRead,
                this.getCreatedAt(),
                this.translationStatus
        );
    }
}