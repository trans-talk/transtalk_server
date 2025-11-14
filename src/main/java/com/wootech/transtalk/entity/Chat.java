package com.wootech.transtalk.entity;

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
public class Chat {
    @Id
    private String id;
    private String originalContent;
    private TranslationStatus translationStatus;
    private String translatedContent;
    private boolean read = false;
    private Long chatroomId;
    private Long senderId;
    private LocalDateTime sendAt = LocalDateTime.now();
}
