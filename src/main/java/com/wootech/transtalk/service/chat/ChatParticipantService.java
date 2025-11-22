package com.wootech.transtalk.service.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.repository.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatParticipantService {
    private final ChatRepository chatRepository;

    @Transactional
    public Optional<ChatMessage> findLastChatByChatRoomId(Long chatRoomId) {
        return chatRepository.findLastByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
    }

    @Transactional
    public int getUnreadCount(Long chatRoomId, Instant lastReadTime) {
        return chatRepository.countByChatRoomIdAndCreateAtAfter(chatRoomId, lastReadTime);
    }
}
