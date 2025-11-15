package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.entity.MongoChat;
import com.wootech.transtalk.enums.TranslationStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatRepositoryMongoAdapter implements ChatRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        MongoChat mongoChat = MongoChat.fromDomain(chatMessage);
        MongoChat savedChat = mongoTemplate.save(mongoChat);
        return savedChat.toDomain();
    }

    /**
     * 채팅방 id로 마지막 채팅을 찾는 메서드입니다.
     *
     * @param chatRoomId
     * @return
     */
    @Override
    public Optional<ChatMessage> findLastByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId) {
        Query query = new Query(Criteria.where("chatroomId").is(chatRoomId))
                .with(Sort.by(Sort.Direction.DESC, "sendAt"))
                .limit(1);

        MongoChat chat = mongoTemplate.findOne(query, MongoChat.class);
        return Optional.of(chat.toDomain());
    }

    /**
     * 채팅방 id와 상대방 id로 마지막 메세지를 찾는 메서드입니다.
     *
     * @param senderId
     * @param chatRoomId
     * @return
     */
    @Override
    public Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId,
                                                                                        Long chatRoomId) {
        return Optional.empty();
    }

    @Override
    public Optional<ChatMessage> updateTranslation(Long chatId, String translatedContent, TranslationStatus status) {
        String chatMongoId = chatId.toString();
        // 업데이트 쿼리
        Update update = new Update()
                .set("translatedContent", translatedContent)
                .set("translationStatus", status);
        Query query = new Query(Criteria.where("_id").is(new ObjectId(chatMongoId)));

        MongoChat updateChat = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                MongoChat.class);

        return Optional.ofNullable(updateChat).map(MongoChat::toDomain);
    }

    @Override
    public Optional<ChatMessage> findById(Long chatId) {
        String chatMongoId = String.valueOf(chatId);
        MongoChat chat = mongoTemplate.findById(chatMongoId, MongoChat.class);
        return Optional.of(chat.toDomain());
    }

}
