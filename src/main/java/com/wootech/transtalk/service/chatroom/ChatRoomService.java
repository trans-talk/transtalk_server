package com.wootech.transtalk.service.chatroom;

import static com.wootech.transtalk.exception.ErrorMessages.*;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.dto.chatroom.CreateChatRoomResponse;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslateLanguage;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.ChatRoomRepository;
import com.wootech.transtalk.service.chat.ChatParticipantService;
import com.wootech.transtalk.service.user.UserService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    private final ChatParticipantService chatParticipantService;

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
    public Page<ChatRoomResponse> findChatRoomsByUserId(Long currentUserId, String name, Pageable pageable) {
        User currentUser = userService.getUserById(currentUserId);
        Page<ChatRoom> chatRooms = chatRoomRepository.findByParticipantsUserId(currentUserId, name, pageable);

        return chatRooms.map(chatRoom -> convertToChatRoomResponse(currentUserId, chatRoom));
    }

    @Transactional
    public ChatRoomResponse updateChatRoomInfo(Long chatRoomId, Long recipientId) {
        ChatRoom findChatRoom = findById(chatRoomId);

        return convertToChatRoomResponse(recipientId, findChatRoom);
    }

    @Transactional(readOnly = true)
    public ChatRoom findById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.error("[ChatRoomService] Received ChatRoom Id={}", chatRoomId);
                    return new NotFoundException(CHAT_ROOM_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404));
                });
    }

    @Transactional
    public ChatRoomResponse convertToChatRoomResponse(Long recipientId, ChatRoom chatRoom) {
        User recipient = chatRoom.getRecipient(recipientId);
        //채팅방의 가장 마지막 메세지
        ChatMessage lastChat = chatParticipantService.findLastChatByChatRoomId(chatRoom.getId()).orElse(null);
        //마지막 읽은 메세지 부터 - 현재 저장된 메세지 개수를 구하는 메서드
        Instant myLastReadTime = chatRoom.getMyLastReadTime(recipientId);
        int unreadCount = chatParticipantService.getUnreadCount(chatRoom.getId(), myLastReadTime);

        return new ChatRoomResponse(
                chatRoom.getId(),
                recipient.getPicture(),
                recipient.getName(),
                chatRoom.getLanguage().getCode(),
                lastChat != null ? lastChat.getOriginalContent() : "",
                lastChat != null ? lastChat.getTranslatedContent() : "",
                lastChat != null ? lastChat.getCreatedAt() : null,
                lastChat != null ? unreadCount : 0);
    }

    @Transactional
    public void updateLastReadChatMessage(String userEmail, Long chatRoomId) {
        ChatRoom chatRoom = findById(chatRoomId);
        User currentUser = userService.getUserByEmail(userEmail);
        User recipient = chatRoom.getRecipient(currentUser.getId());

        chatRoom.exit(currentUser.getId());
    }
}