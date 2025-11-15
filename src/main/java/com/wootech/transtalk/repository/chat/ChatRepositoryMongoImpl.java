package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.entity.MongoChat;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

//@Repository
@RequiredArgsConstructor
public class ChatRepositoryMongoImpl implements ChatRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        MongoChat mongoChat = MongoChat.fromDomain(chatMessage);
        MongoChat savedChat = mongoTemplate.save(mongoChat);
        return savedChat.toDomain();
    }

    /**
     * 채팅방 id로 마지막 채팅을 찾는 메서드입니다.
     * @param chatRoomId
     * @return
     */
    @Override
    public Optional<ChatMessage> findLastByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId) {
        return Optional.empty();
    }

    /**
     * 채팅방 id와 상대방 id로 마지막 메세지를 찾는 메서드입니다.
     * @param senderId
     * @param chatRoomId
     * @return
     */
    @Override
    public Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId,
                                                                                        Long chatRoomId) {
        return Optional.empty();
    }
}
