package com.wootech.transtalk.service.chat;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.dto.chat.ChatMessageRequest;
import com.wootech.transtalk.dto.chat.RecipientInfoRequest;
import com.wootech.transtalk.dto.chat.mongo.MongoChatMessageListResponse;
import com.wootech.transtalk.dto.chat.mongo.MongoChatMessageResponse;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslationStatus;
import com.wootech.transtalk.repository.chat.ChatRepositoryMongoAdapter;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import com.wootech.transtalk.service.translate.TranslationService;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.Principal;

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
                    new MongoChatMessageResponse(
                            translatedChat.getId(),
                            translatedChat.getOriginalContent(),
                            translatedChat.getTranslatedContent(),
                            translatedChat.getTranslationStatus(),
                            senderEmail,
                            translatedChat.isRead(),
                            translatedChat.getCreatedAt()

                    )
            );
            log.info("[ChatService] Updated Translation Status={}", translatedChat.getTranslationStatus());
        } catch (Exception e) {
            log.error("[ChatService] Translation Failed With Chat: {}", chat.getId(), e);
        }
    }

    public MongoChatMessageResponse saveChat(Principal principal, ChatMessageRequest request, Long chatRoomId) {
        String senderEmail = principal.getName();
        User sender = userService.getUserByEmail(senderEmail);
        ChatRoom foundChatRoom = chatRoomService.findById(chatRoomId);

        ChatMessage chatMessage = new ChatMessage(
                null,
                request.content(),
                null,
                chatRoomId,
                sender.getId(),
                senderEmail,
                false,
                null,
                TranslationStatus.PENDING
        );

        // 1) 몽고 DB에 메시지 저장
        ChatMessage savedChat = chatRepositoryMongoAdapter.save(chatMessage);

        // 2) 번역되기 전 메시지 즉시 클라이언트로 전송
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId,
                new MongoChatMessageResponse(
                        savedChat.getId(),
                        savedChat.getOriginalContent(),
                        null,
                        TranslationStatus.PENDING,
                        senderEmail,
                        savedChat.isRead(),
                        savedChat.getCreatedAt()
                )
        );

        // 3) 비동기 번역 시작
        translateAndUpdate(savedChat, foundChatRoom.getLanguage().getCode(), senderEmail);

        return new MongoChatMessageResponse(
                savedChat.getId(),
                savedChat.getOriginalContent(),
                savedChat.getTranslatedContent(),
                savedChat.getTranslationStatus(),
                senderEmail,
                savedChat.isRead(),
                savedChat.getCreatedAt()
        );
    }

    public MongoChatMessageListResponse getChats(AuthUser authUser, Long chatRoomId, Pageable pageable) {
        userService.getUserById(authUser.getUserId());

        ChatRoom chatRoom = chatRoomService.findById(chatRoomId);
        User recipient = chatRoom.getRecipient(authUser.getUserId());

        Page<ChatMessage> chats = chatRepositoryMongoAdapter.findAllByChatRoomIdOrderByCreatedAt(chatRoomId, pageable);

        Page<MongoChatMessageResponse> responses = chats.map(MongoChatMessageResponse::from);

        RecipientInfoRequest recipientInfo = new RecipientInfoRequest(recipient.getPicture(), recipient.getEmail(),
                recipient.getName());

        return MongoChatMessageListResponse.from(responses, recipientInfo);
    }


}
