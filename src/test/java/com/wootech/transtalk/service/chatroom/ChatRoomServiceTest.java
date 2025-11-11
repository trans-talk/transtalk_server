package com.wootech.transtalk.service.chatroom;


import static org.assertj.core.groups.Tuple.tuple;

import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.repository.UserRepository;
import com.wootech.transtalk.service.chat.ChatService;
import com.wootech.transtalk.service.user.UserService;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
@DisplayName("Integration - ChatRoomService")
class ChatRoomServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatService chatService;

    @Test
    void findChatRoomsByUserId() {
        User user = userService.findByEmailOrGet(
                "tae@google", "tae", UserRole.ROLE_USER, "img1");
        User recipient = userService.findByEmailOrGet(
                "other@google", "other", UserRole.ROLE_USER, "img2");
        Long savedChatRoomId = chatRoomService.save("ko", "tae@google", "other@google");
        chatService.save(new ChatMessageRequest("hello"), savedChatRoomId, "tae@google");
        Pageable pageable = PageRequest.of(0, 40);
        Page<ChatRoomResponse> responses = chatRoomService.findChatRoomsByUserId(user.getId(),pageable);

        Assertions.assertThat(responses.getContent())
                .extracting(
                        ChatRoomResponse::originalRecentMessage,
                        ChatRoomResponse::unreadMessageCount,
                        ChatRoomResponse::chatroomId,
                        ChatRoomResponse::recipientName,
                        ChatRoomResponse::recipientPicture,
                        ChatRoomResponse::selectedLanguage,
                        ChatRoomResponse::translatedRecentMessage
                ).containsExactlyInAnyOrder(
                        tuple(
                                "hello",
                                1,
                                savedChatRoomId,
                                recipient.getName(),
                                recipient.getPicture(),
                                "ko",
                                (String) null
                        )
                );
    }
}