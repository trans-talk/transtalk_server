package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.enums.TranslationStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRepository {
    ChatMessage save(ChatMessage chatMessage);

    Optional<ChatMessage> findLastByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId, Long chatRoomId);

    ChatMessage updateTranslation(ChatMessage changeChat);

    Optional<ChatMessage> findById(Long chatId);

    Page<ChatMessage> findAllByChatRoomIdOrderByCreatedAt(Long chatRoomId, Pageable pageable);

    int countByChatRoomIdAndCreateAtAfter(Long chatRoomId, Instant lastReadTime);
}
