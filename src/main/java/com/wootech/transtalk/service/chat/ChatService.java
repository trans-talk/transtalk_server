package com.wootech.transtalk.service.chat;


import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.dto.chat.ChatMessageListResponse;
import com.wootech.transtalk.dto.chat.ChatMessageResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.dto.chat.RecipientInfoRequest;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslationStatus;
import com.wootech.transtalk.repository.chat.ChatRepository;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import com.wootech.transtalk.service.translate.TranslationService;
import com.wootech.transtalk.service.user.UserService;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final TranslationService translationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatMessageResponse save(String content, Long chatRoomId, String senderEmail) {
        User sender = userService.getUserByEmail(senderEmail);
        ChatRoom findChatRoom = chatRoomService.findById(chatRoomId);

        ChatMessage chatMessage = new ChatMessage(
                null,
                content,
                null,
                chatRoomId,
                sender.getId(),
                senderEmail,
                false,
                null,
                TranslationStatus.PENDING
        );
        ChatMessage savedChat = chatRepository.save(chatMessage);

        translate(savedChat, findChatRoom.getLanguage().getCode(), senderEmail);

        return new ChatMessageResponse(
                savedChat.getId(),
                savedChat.getOriginalContent(),
                savedChat.getTranslatedContent(),
                sender.getEmail(),
                savedChat.getCreatedAt(),
                savedChat.isRead(),
                savedChat.getTranslationStatus());
    }

    @Transactional
    public void translate(ChatMessage chat, String targetLangCode, String senderEmail) {
        try {
            String translatedContent = translationService.translate(chat.getOriginalContent(), targetLangCode);
            log.info("[ChatService] Translated Content: {}", translatedContent);

            ChatMessage translatedChat = updateTranslatedContent(chat, translatedContent);

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chat.getChatRoomId(),
                    new ChatMessageResponse(
                            translatedChat.getId(),
                            translatedChat.getOriginalContent(),
                            translatedChat.getTranslatedContent(),
                            senderEmail,
                            translatedChat.getCreatedAt(),
                            translatedChat.isRead(),
                            translatedChat.getTranslationStatus()
                    ));
            log.info("[ChatService] Updated Translation Status={}", translatedChat.getTranslationStatus());
        } catch (Exception e) {
            log.error("[ChatService] Translation Failed With Chat: {}", chat.getId(), e);
        }
    }

    @Transactional
    public ChatMessage updateTranslatedContent(ChatMessage chat,String translatedContent) {
        chat.completeTranslate(translatedContent);

        return chatRepository.updateTranslation(chat);
    }

    @Transactional
    public ChatMessageListResponse getChats(AuthUser authUser, Long chatRoomId, Pageable pageable) {
        userService.getUserById(authUser.getUserId());

        ChatRoom chatRoom = chatRoomService.findById(chatRoomId);
        User recipient = chatRoom.getRecipient(authUser.getUserId());

        Page<ChatMessageResponse> responses = chatRepository.findAllByChatRoomIdOrderByCreatedAt(
                chatRoomId, pageable).map(ChatMessageResponse::from
        );

        RecipientInfoRequest recipientInfo = new RecipientInfoRequest(
                recipient.getPicture(),
                recipient.getEmail(),
                recipient.getName());

        return ChatMessageListResponse.from(responses, recipientInfo);
    }

}