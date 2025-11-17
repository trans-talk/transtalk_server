package com.wootech.transtalk.service.chat;

import static com.wootech.transtalk.exception.ErrorMessages.CHAT_NOT_FOUND_ERROR;
import static com.wootech.transtalk.exception.ErrorMessages.CHAT_ROOM_NOT_FOUND_ERROR;

import com.mongodb.client.result.UpdateResult;
import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.MongoChat;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslationStatus;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.ChatRoomRepository;
import com.wootech.transtalk.repository.chat.ChatRepositoryMongoAdapter;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
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
@Service
@RequiredArgsConstructor
public class MongoChatService {
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final TranslationService translationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepositoryMongoAdapter chatRepositoryMongoAdapter;

    @Async
    public void translateAndUpdate(ChatMessage chat, String targetLangCode, String senderEmail) {
        try {
            String translatedContent = translationService.translate(chat.getOriginalContent(), targetLangCode);
            log.info("[ChatService] Translated Content: {}", translatedContent);

            chat.completeTranslate(translatedContent);
            ChatMessage translatedChat = chatRepositoryMongoAdapter.updateTranslation(chat);

            // 클라이언트에 메세지 전송 - 번역 완료된 메세지, 상태
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chat.getChatRoomId(),
                    new ChatMessageResponse(
                            Long.valueOf(translatedChat.getId()),
                            translatedChat.getOriginalContent(),
                            translatedChat.getTranslatedContent(),
                            senderEmail,
                            translatedChat.getCreatedAt(),
                            0,
                            translatedChat.getTranslationStatus()
                    )
            );
            log.info("[ChatService] Updated Translation Status={}", translatedChat.getTranslationStatus());
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
        User sender = userService.getUserByEmail(senderEmail);
        ChatRoom foundChatRoom = chatRoomService.findById(chatRoomId);

        ChatMessage chatMessage = new ChatMessage(null,
                request.content(),
                null,
                chatRoomId,
                sender.getId(),
                senderEmail,
                1,
                LocalDateTime.now(),
                TranslationStatus.PENDING
        );

        // 몽고 디비 먼저 저장
        ChatMessage savedChat = chatRepositoryMongoAdapter.save(chatMessage);

        // 비동기 번역 시작
        translateAndUpdate(savedChat, foundChatRoom.getLanguage().getCode(), senderEmail);

        // 완료된 결과값
        return new ChatMessageResponse(
                Long.valueOf(savedChat.getId()),
                savedChat.getOriginalContent(),
                savedChat.getTranslatedContent(),
                senderEmail,
                savedChat.getCreatedAt(),
                0,
                savedChat.getTranslationStatus()
        );
    }
    // 마지막 채팅 조회 메서드
    public void findLastChat(Long chatRoomId) {
        ChatMessage chatMessage = chatRepositoryMongoAdapter.findLastByChatRoomIdOrderByCreatedAtDesc(chatRoomId)
                .orElseThrow(() -> new RuntimeException(""));
        //TODO 응답 DTO가 필요합니다.
    }

    // 안 읽은 메세지 조회 메서드 -> 필요 없을 것 같습니다.
    public void countUnreadChats(Long chatRoomId) {
        Criteria criteria = new Criteria()
                .andOperator(
                        Criteria.where("chatroomId").is(chatRoomId),
                        Criteria.where("read").is(false)
                );

        Query query = new Query(criteria);
//        return mongoTemplate.count(query, MongoChat.class);
    }
}
