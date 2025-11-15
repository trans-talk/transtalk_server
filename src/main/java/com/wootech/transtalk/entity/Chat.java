package com.wootech.transtalk.entity;

import static com.wootech.transtalk.exception.ErrorMessages.DUPLICATE_TRANSLATION_ERROR;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import com.wootech.transtalk.event.Events;
import com.wootech.transtalk.event.MessageNotificationEvent;
import com.wootech.transtalk.exception.custom.ConflictException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

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
    private int unreadCount;
    @Column(nullable = false)
    private Long senderId;
    @Column(nullable = false)
    private Long chatRoomId;
    private TranslationStatus translationStatus;

    public Chat(String originalContent, Long senderId, Long chatRoomId) {
        this.originalContent = originalContent;
        this.unreadCount = 1;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.translationStatus = TranslationStatus.PENDING;
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

    public static Chat fromDomain(ChatMessage chatMessage) {
        Chat entity = new Chat(
                chatMessage.getOriginalContent(),
                chatMessage.getSenderId(),
                chatMessage.getChatRoomId()
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
                false,
                LocalDateTime.ofInstant(this.getCreatedAt(), ZoneId.of("Asia/Seoul")),
                this.translationStatus
        );
    }
}