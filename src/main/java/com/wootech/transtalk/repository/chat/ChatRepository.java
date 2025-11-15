package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import java.util.Optional;

public interface ChatRepository {
    ChatMessage save(ChatMessage chatMessage);

    Optional<ChatMessage> findLastByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId, Long chatRoomId);

    Optional<ChatMessage> updateTranslation(Long chatId, String translatedContent, TranslationStatus status);

    Optional<ChatMessage> findById(Long chatId);
}
