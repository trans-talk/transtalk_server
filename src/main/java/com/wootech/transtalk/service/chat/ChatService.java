package com.wootech.transtalk.service.chat;


import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslationStatus;
import com.wootech.transtalk.repository.chat.ChatRepository;
import com.wootech.transtalk.repository.chat.ChatRepositoryJpaAdapter;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import com.wootech.transtalk.service.translate.TranslationService;
import com.wootech.transtalk.service.user.UserService;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final TranslationService translationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatMessageResponse save(String message, Long chatRoomId, String senderEmail) {
        User sender = userService.getUserByEmail(senderEmail);
        ChatRoom findChatRoom = chatRoomService.findById(chatRoomId);

        ChatMessage chatMessage = new ChatMessage(null,
                message,
                null,
                chatRoomId,
                sender.getId(),
                1,
                null,
                TranslationStatus.PENDING
        );
        ChatMessage savedChat = chatRepository.save(chatMessage);

        translate(savedChat, findChatRoom.getLanguage().getCode(), senderEmail);

        return new ChatMessageResponse(
                Long.valueOf(savedChat.getId()),
                savedChat.getOriginalContent(),
                savedChat.getTranslatedContent(),
                sender.getEmail(),
                savedChat.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant(),
                0,
                savedChat.getTranslationStatus());
    }

    @Transactional
    public void translate(ChatMessage chat, String targetLangCode, String senderEmail) {
        String translatedContent = translationService.translate(chat.getOriginalContent(), targetLangCode);

        ChatMessage translatedChat = updateTranslatedContent(chat, translatedContent);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chat.getChatRoomId(),
                new ChatMessageResponse(
                        Long.valueOf(translatedChat.getId()),
                        translatedChat.getOriginalContent(),
                        translatedChat.getTranslatedContent(),
                        senderEmail,
                        translatedChat.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant(),
                        0,
                        translatedChat.getTranslationStatus()
                ));
    }

    @Transactional
    public ChatMessage updateTranslatedContent(ChatMessage chat,String translatedContent) {
        chat.completeTranslate(translatedContent);

        return chatRepository.updateTranslation(chat);
    }

    public void getChats() {

        //DTO로 반환
    }

}