package com.wootech.transtalk.entity;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Document(collection = "chat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MongoChat {
    @Id // object id
    private String id;
    private String originalContent;
    private TranslationStatus translationStatus;
    private String translatedContent;
    private boolean isRead;
    private Long chatroomId;
    private Long senderId;
    private String senderEmail;
    private Instant createdAt;

    // 매핑: domain class -> mongo document
    public static MongoChat fromDomain(ChatMessage chatMessage) {
        return MongoChat.builder()
                .originalContent(chatMessage.getOriginalContent())
                .translatedContent(chatMessage.getTranslatedContent())
                .translationStatus(chatMessage.getTranslationStatus())
                .senderId(chatMessage.getSenderId())
                .senderEmail(chatMessage.getSenderEmail())
                .chatroomId(chatMessage.getChatRoomId())
                .isRead(chatMessage.isRead())
                .createdAt(Instant.now())
                .build();
    }

    public ChatMessage toDomain() {
        return new ChatMessage(
                this.id,
                this.originalContent,
                this.translatedContent,
                this.chatroomId,
                this.senderId,
                this.senderEmail,
                this.isRead,
                this.createdAt,
                this.translationStatus
        );
    }
}