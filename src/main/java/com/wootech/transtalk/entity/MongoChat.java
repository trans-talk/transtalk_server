package com.wootech.transtalk.entity;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "chat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MongoChat {
    @Id
    private String id;
    private String originalContent;
    private TranslationStatus translationStatus;
    private String translatedContent;
    private boolean isRead;
    private Long chatroomId;
    private Long senderId;
    private String senderEmail;
    private LocalDateTime sendAt = LocalDateTime.now();

    public static MongoChat fromDomain(ChatMessage chatMessage) {
        return MongoChat.builder()
                .originalContent(chatMessage.getOriginalContent())
                .translatedContent(null) // 비동기 처리
                .translationStatus(TranslationStatus.PENDING) // 대기 상태
                .senderId(chatMessage.getSenderId())
                .senderEmail(chatMessage.getSenderEmail())
                .chatroomId(chatMessage.getChatRoomId())
                .isRead(chatMessage.isRead())
                .sendAt(LocalDateTime.now())
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
                this.sendAt,
                this.translationStatus
        );
    }
}