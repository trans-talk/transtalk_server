package com.wootech.transtalk.service.chat;

import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.repository.ChatRoomRepository;
import com.wootech.transtalk.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(String language, String senderEmail, String recipientEmail) {
        ChatRoom chatRoom = new ChatRoom(language);

        User sender = userRepository.findByEmail(senderEmail).orElseThrow(() -> new RuntimeException(""));
        User recipient = userRepository.findByEmail(recipientEmail).orElseThrow(() -> new RuntimeException(""));

        new Participant(sender, chatRoom);
        new Participant(recipient, chatRoom);

        chatRoomRepository.save(chatRoom);
    }
    public ChatRoom findById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException(""));
    }
}
