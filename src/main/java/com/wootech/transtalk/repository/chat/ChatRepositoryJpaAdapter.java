package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.entity.Chat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@RequiredArgsConstructor
public class ChatRepositoryJpaAdapter implements ChatRepository {

    private final ChatJpaRepository jpaRepository;

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
    public Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId,
                                                                                        Long chatRoomId) {
        return jpaRepository.findTopBySenderIdAndChatRoomIdOrderByCreatedAtDesc(senderId, chatRoomId)
                .map(Chat::toDomain);
    }

    @Override
    public ChatMessage updateTranslation(ChatMessage changeChat) {
        Chat chat = jpaRepository.findById(Long.valueOf(changeChat.getId())).get();
        chat.applyDomain(changeChat);
        return chat.toDomain();
    }

    @Override
    public Optional<ChatMessage> findById(Long chatId) {
        return jpaRepository.findById(chatId).map(Chat::toDomain);
    }

    @Override
    public Page<ChatMessage> findAllByChatRoomIdOrderByCreatedAt(Long chatRoomId, Pageable pageable) {
        Page<Chat> chatPage = jpaRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable);

        return chatPage.map(chat -> chat.toDomain());
    }


}
