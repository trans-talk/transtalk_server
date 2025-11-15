package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import java.util.Optional;

public interface ChatRepositoryCustom {
    ChatMessage save(ChatMessage chatMessage);

    Optional<ChatMessage> findLastByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId, Long chatRoomId);
}
