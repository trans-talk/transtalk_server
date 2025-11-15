package com.wootech.transtalk.entity;

import static com.wootech.transtalk.exception.ErrorMessages.DUPLICATE_TRANSLATION_ERROR;

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
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
    private TranslationStatus translationStatus;

    public Chat(String originalContent, User sender, ChatRoom chatRoom) {
        this.originalContent = originalContent;
        this.unreadCount = 1;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.translationStatus = TranslationStatus.PENDING;
    }

    public void completeTranslate(String translatedContent) {
        completeTranslate();
        this.translatedContent = translatedContent;
        Events.raise(new MessageNotificationEvent(chatRoom.getId(), sender.getId()));
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