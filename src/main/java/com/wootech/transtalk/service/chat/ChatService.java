package com.wootech.transtalk.service.chat;

import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.ChatMessageResponse;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.repository.ChatRepository;
import com.wootech.transtalk.repository.UserRepository;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    @Transactional
    public ChatMessageResponse save(ChatMessageRequest request, Long chatRoomId) {
        User sender = userRepository.findByEmail(request.userEmail()).orElseThrow(() -> new RuntimeException(""));
        ChatRoom findChatRoom = chatRoomService.findById(chatRoomId);
        Chat chat = new Chat(request.content(), sender, findChatRoom);
        chatRepository.save(chat);
        return new ChatMessageResponse(chat.getOriginalContent(), chat.getTranslatedContent(), sender.getEmail());
    }
}
