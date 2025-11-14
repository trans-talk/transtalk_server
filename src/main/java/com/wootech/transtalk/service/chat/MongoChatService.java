package com.wootech.transtalk.service.chat;

import static com.wootech.transtalk.exception.ErrorMessages.CHAT_ROOM_NOT_FOUND_ERROR;

import com.mongodb.client.result.UpdateResult;
import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.MongoChat;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslationStatus;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.ChatRoomRepository;
import com.wootech.transtalk.service.translate.TranslationService;
import com.wootech.transtalk.service.user.UserService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
@RequiredArgsConstructor
public class MongoChatService {
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final MongoTemplate mongoTemplate;
    private final TranslationService translationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public void translateAndUpdate(MongoChat chat, String targetLangCode, String senderEmail) {
        try {
            String translated = translationService.translate(chat.getOriginalContent(), targetLangCode);
            log.info("[ChatService] Translated Content: {}", translated);

            // 업데이트 쿼리
            Update update = new Update()
                    .set("translatedContent", translated)
                    .set("translationStatus", TranslationStatus.COMPLETED);
            Query query = new Query(Criteria.where("_id").is(new ObjectId(chat.getId())));
            UpdateResult result = mongoTemplate.updateFirst(query, update, MongoChat.class);
            log.info("[ChatService] ModifiedCount={}", result.getModifiedCount());

            MongoChat updated = mongoTemplate.findById(chat.getId(), MongoChat.class);

            // 클라이언트에 메세지 전송 - 번역 완료된 메세지, 상태
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chat.getChatroomId(),
                    new ChatMessageResponse(
                            Long.valueOf(updated.getId()),
                            updated.getOriginalContent(),
                            updated.getTranslatedContent(),
                            senderEmail,
                            updated.getSendAt().atZone(ZoneId.systemDefault()).toInstant(),
                            0,
                            updated.getTranslationStatus()
                    )
            );
            log.info("[ChatService] Updated Translation Status={}", updated.getTranslationStatus());
        } catch (Exception e) {
            log.error("[ChatService] Translation Failed With Chat: {}", chat.getId(), e);
        }
    }
    /**
     * 기능
     * 1. chat 저장
     * 2. chat 전송 - 번역 대기
     * 3. chat 번역
     * 4. 번역 완료된 chat 전송
     **/
    public ChatMessageResponse saveChat(Principal principal, ChatMessageRequest request, Long chatRoomId) {
        String senderEmail = principal.getName();
        User foundUser = userService.getUserByEmail(senderEmail);
        ChatRoom foundChatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(CHAT_ROOM_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404)));

        String originalContent = request.content();

        MongoChat chat = MongoChat.builder()
                .originalContent(originalContent)
                .translatedContent(null) // 비동기 처리
                .translationStatus(TranslationStatus.PENDING) // 대기 상태
                .senderId(foundUser.getId())
                .chatroomId(chatRoomId)
                .sendAt(LocalDateTime.now())
                .build();

        // 몽고 디비 먼저 저장
        MongoChat savedChat = mongoTemplate.save(chat);

        // 비동기 번역 시작
        translateAndUpdate(savedChat, foundChatRoom.getLanguage().getCode(), senderEmail);

        // 완료된 결과값
        return new ChatMessageResponse(
                Long.valueOf(savedChat.getId()),
                savedChat.getOriginalContent(),
                savedChat.getTranslatedContent(),
                senderEmail,
                savedChat.getSendAt().atZone(ZoneId.systemDefault()).toInstant(),
                0,
                savedChat.getTranslationStatus()
        );
    }
    // 마지막 채팅 조회 메서드
    public MongoChat findLastChat(Long chatRoomId) {

        Query query = new Query(Criteria.where("chatroomId").is(chatRoomId))
                .with(Sort.by(Sort.Direction.DESC, "sendAt"))
                .limit(1);

        return mongoTemplate.findOne(query, MongoChat.class);
    }

    // 안 읽은 메세지 조회 메서드
    public long countUnreadChats(Long chatRoomId) {
        Criteria criteria = new Criteria()
                .andOperator(
                        Criteria.where("chatroomId").is(chatRoomId),
                        Criteria.where("read").is(false)
                );

        Query query = new Query(criteria);
        return mongoTemplate.count(query, MongoChat.class);
    }
}
