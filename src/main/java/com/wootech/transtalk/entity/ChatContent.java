package com.wootech.transtalk.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "chat_content")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatContent {
    @Id
    private String id;
    private String originalContent;
    private String translatedContent;
    private Integer unreadCount;
    private Long chatroomId;
    private Long senderId;
    private LocalDateTime sendAt = LocalDateTime.now();
}
