package com.wootech.transtalk.service.chatroom;

import static com.wootech.transtalk.exception.ErrorMessages.*;

import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.ChatRepository;
import com.wootech.transtalk.repository.ChatRoomRepository;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    private final ChatRepository chatRepository;

    @Transactional
    public Long save(String language, String senderEmail, String recipientEmail) {
        ChatRoom chatRoom = new ChatRoom(language);

        User sender = userService.getUserByEmail(senderEmail);
        User recipient = userService.getUserByEmail(recipientEmail);

        new Participant(sender, chatRoom);
        new Participant(recipient, chatRoom);

        return chatRoomRepository.save(chatRoom).getId();
    }

    @Transactional
    public Page<ChatRoomResponse> findChatRoomsByUserId(Long currentUserId, Pageable pageable) {
        User currentUser = userService.getUserById(currentUserId);
        Page<ChatRoom> chatRooms = chatRoomRepository.findByParticipantsUserId(currentUserId, pageable);

        return chatRooms.map(chatRoom -> {
            User recipient = chatRoom.getRecipient(currentUserId);

            Chat lastChat = chatRepository.findTopByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId());
            long lastReadChatId = chatRoom.getLastReadChatId(currentUserId);

            return new ChatRoomResponse(chatRoom.getId(), recipient.getPicture(),
                    recipient.getName(), chatRoom.getLanguage(), lastChat.getOriginalContent(),
                    lastChat.getTranslatedContent(),
                    lastChat.getCreatedAt(), (int) (lastChat.getId() - lastReadChatId));
        });
    }

    public ChatRoom findById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.error("[ChatRoomService] Received ChatRoom Id={}", chatRoomId);
                    return new NotFoundException(CHAT_ROOM_NOT_FOUND_ERROR);
                });
    }
}
