package com.wootech.transtalk.service.chatroom;

import static com.wootech.transtalk.exception.ErrorMessages.*;

import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.dto.chatroom.CreateChatRoomResponse;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslateLanguage;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.ChatRoomRepository;
import com.wootech.transtalk.service.chat.ChatService;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    private final ChatService chatService;

    @Transactional
    public CreateChatRoomResponse save(TranslateLanguage language, String senderEmail, String recipientEmail) {
        User sender = userService.getUserByEmail(senderEmail);
        User recipient = userService.getUserByEmail(recipientEmail);
        ChatRoom chatRoom = chatRoomRepository.findChatRoomBetweenUsers(sender.getId(), recipient.getId(), language)
                .orElseGet(() -> {
                    ChatRoom newChatRoom = new ChatRoom(language);
                    new Participant(sender, newChatRoom);
                    new Participant(recipient, newChatRoom);
                    return chatRoomRepository.save(newChatRoom);
                });
        return new CreateChatRoomResponse(chatRoom.getId());
    }

    @Transactional
    public Page<ChatRoomResponse> findChatRoomsByUserId(Long currentUserId, Pageable pageable) {
        User currentUser = userService.getUserById(currentUserId);
        Page<ChatRoom> chatRooms = chatRoomRepository.findAllByUserId(currentUserId, pageable);

        Page<ChatRoomResponse> chatRoomResponsePage = chatRooms.map(chatRoom -> {
            User recipient = chatRoom.getRecipient(currentUserId);
            Chat lastChat = chatService.findLastChat(chatRoom.getId());
            long unreadCount = chatService.countUnreadChats(chatRoom.getId());

            return ChatRoomResponse.builder()
                    .chatroomId(chatRoom.getId())
                    .originalRecentMessage(lastChat.getOriginalContent())
                    .translatedRecentMessage(lastChat.getTranslatedContent())
                    .selectedLanguage(chatRoom.getLanguage().getCode())
                    .recentMessageTime(Instant.from(lastChat.getSendAt()))
                    .recipientName(recipient.getName())
                    .recipientPicture(recipient.getPicture())
                    .unreadMessageCount(unreadCount)
                    .build();
        });

        return chatRoomResponsePage;
    }

    public ChatRoom findById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.error("[ChatRoomService] Received ChatRoom Id={}", chatRoomId);
                    return new NotFoundException(CHAT_ROOM_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404));
                });
    }
}

