package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.repository.ChatRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryJpaImpl implements ChatRepositoryCustom {

    private final ChatRepository jpaRepository;
    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        Chat chat = Chat.fromDomain(chatMessage);
        jpaRepository.save(chat);
        return chat.toDomain();
    }

    @Override
    public Optional<ChatMessage> findLastByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId) {
        return jpaRepository.findTopByChatRoomIdOrderByCreatedAtDesc(
                chatRoomId).map(Chat::toDomain);
    }

    @Override
    public Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId, Long chatRoomId) {
        return jpaRepository.findTopBySenderIdAndChatRoomIdOrderByCreatedAtDesc(senderId, chatRoomId)
                .map(Chat::toDomain);
    }

}
