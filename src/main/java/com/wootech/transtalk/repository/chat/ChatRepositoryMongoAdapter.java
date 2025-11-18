package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.entity.MongoChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class ChatRepositoryMongoAdapter implements ChatRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        MongoChat mongoChat = MongoChat.fromDomain(chatMessage);
        MongoChat savedChat = mongoTemplate.save(mongoChat);
        return savedChat.toDomain();
    }

    // 채팅방 id로 마지막 채팅 찾기
    @Override
    public Optional<ChatMessage> findLastByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId) {
        Query query = new Query(Criteria.where("chatroomId").is(chatRoomId))
                .with(Sort.by(Sort.Direction.DESC, "sendAt"))
                .limit(1);

        MongoChat chat = mongoTemplate.findOne(query, MongoChat.class);

        return Optional.ofNullable(chat)
                .map(MongoChat::toDomain);
    }


    // 채팅방 id와 상대방 id로 마지막 메세지를 찾기
    @Override
    public Optional<ChatMessage> findLastByRecipientIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId,
                                                                                        Long chatRoomId) {
        Query query = new Query(
                Criteria.where("chatroomId").is(chatRoomId)
                        .and("senderId").is(senderId)
        )
                .with(Sort.by(Sort.Direction.DESC, "sendAt"))
                .limit(1);

        MongoChat chat = mongoTemplate.findOne(query, MongoChat.class);

        return Optional.ofNullable(chat)
                .map(MongoChat::toDomain);
    }


    @Override
    public Page<ChatMessage> findAllByChatRoomIdOrderByCreatedAt(Long chatRoomId, Pageable pageable) {
        Query query = new Query(
                Criteria.where("chatroomId").is(chatRoomId)
        )
                .with(pageable)
                .with(Sort.by(Sort.Direction.ASC, "sendAt"));

        List<MongoChat> chatList = mongoTemplate.find(query, MongoChat.class);
        long total = mongoTemplate.count(
                new Query(Criteria.where("chatroomId").is(chatRoomId)),
                MongoChat.class
        );

        List<ChatMessage> results = chatList.stream()
                .map(MongoChat::toDomain)
                .toList();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }

    @Override
    public ChatMessage updateTranslation(ChatMessage changeChat) {
        String chatMongoId = changeChat.getId();
        // 업데이트 쿼리
        Update update = new Update()
                .set("translatedContent", changeChat.getTranslatedContent())
                .set("translationStatus", changeChat.getTranslationStatus());
        Query query = new Query(Criteria.where("_id").is(new ObjectId(chatMongoId)));

        MongoChat updateChat = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                MongoChat.class);

        return updateChat.toDomain();
    }

    @Override
    public Optional<ChatMessage> findById(Long chatId) {
        String chatMongoId = String.valueOf(chatId);
        MongoChat chat = mongoTemplate.findById(chatMongoId, MongoChat.class);
        return Optional.ofNullable(chat)
                .map(MongoChat::toDomain);
    }

    // 안 읽은 메세지 수 조회 메서드
    public long countUnreadChats(Long chatRoomId) {
        Criteria criteria = new Criteria()
                .andOperator(
                        Criteria.where("chatroomId").is(chatRoomId),
                        Criteria.where("isRead").is(false)
                );

        Query query = new Query(criteria);
        return mongoTemplate.count(query, MongoChat.class);
    }


}
