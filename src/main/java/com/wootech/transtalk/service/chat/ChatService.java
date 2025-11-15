package com.wootech.transtalk.service.chat;

import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.repository.chat.ChatJpaRepository;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import com.wootech.transtalk.service.translate.TranslationService;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatJpaRepository chatJpaRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final TranslationService translationService;

    @Transactional
    public ChatMessageResponse save(String message, Long chatRoomId, String senderEmail) {
        User sender = userService.getUserByEmail(senderEmail);
        ChatRoom findChatRoom = chatRoomService.findById(chatRoomId);

        Chat chat = new Chat(message, sender.getId(), findChatRoom.getId());
        chatJpaRepository.save(chat);

        //TODO 성능에 문제가 있다면 비동기로 변경한다.
        String translatedContent = translationService.translate(message, findChatRoom.getLanguage().getCode());
        chat.completeTranslate(translatedContent);

        return new ChatMessageResponse(
                chat.getId(),
                chat.getOriginalContent(),
                chat.getTranslatedContent(),
                sender.getEmail(),
                chat.getCreatedAt(),
                chat.getUnreadCount(),
                chat.getTranslationStatus());
    }
}