package com.wootech.transtalk.entity;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MongoChatTest {

    @Test
    void ChatMessage_값을_MongoChat으로_저장할때_필드값이_null이어도_NPE가_발생하지_않는다() {
        ChatMessage chat = new ChatMessage(
                "1",
                "hello",
                null, // translatedContent
                1L,
                1L,
                "test@test.com",
                false,
                null,
                TranslationStatus.PENDING
        );

        MongoChat mongoChat = MongoChat.fromDomain(chat);

        assertNull(mongoChat.getTranslatedContent());
        assertEquals("hello", mongoChat.getOriginalContent());
    }


}