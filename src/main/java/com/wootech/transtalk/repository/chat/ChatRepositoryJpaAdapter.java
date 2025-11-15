package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.enums.TranslationStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
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
    public Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId, Long chatRoomId) {
        return jpaRepository.findTopBySenderIdAndChatRoomIdOrderByCreatedAtDesc(senderId, chatRoomId)
                .map(Chat::toDomain);
    }

    @Override
    public Optional<ChatMessage> updateTranslation(Long chatId, String translatedContent, TranslationStatus status) {
        return null;
    }

    @Override
    public Optional<ChatMessage> findById(Long chatId) {

        return null;
    }


}
