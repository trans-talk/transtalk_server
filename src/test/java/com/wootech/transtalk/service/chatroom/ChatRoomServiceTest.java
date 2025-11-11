package com.wootech.transtalk.service.chatroom;


import static org.assertj.core.groups.Tuple.tuple;

import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.dto.chatroom.CreateChatRoomResponse;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslateLanguage;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.service.chat.ChatService;
import com.wootech.transtalk.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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


    User user;
    User recipient;

    @BeforeEach
    void setUp() {
        user = userService.findByEmailOrGet(
                "tae@google", "tae", UserRole.ROLE_USER, "img1");
        recipient = userService.findByEmailOrGet(
                "other@google", "other", UserRole.ROLE_USER, "img2");
    }

    @Test
    void findChatRoomsByUserId() {
        CreateChatRoomResponse response = chatRoomService.save(TranslateLanguage.KOREAN, "tae@google", "other@google");
        chatService.save(new ChatMessageRequest("hello"), response.chatRoomId(), "tae@google");
        Pageable pageable = PageRequest.of(0, 40);
        Page<ChatRoomResponse> responses = chatRoomService.findChatRoomsByUserId(user.getId(), pageable);

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
                                response.chatRoomId(),
                                recipient.getName(),
                                recipient.getPicture(),
                                TranslateLanguage.KOREAN.getCode(),
                                (String) null
                        )
                );
    }

    @Test
    @DisplayName("동일한 회원 사이의 동일한 언어로 채팅방 요청이 들어오면, 기존 채팅방 id를 반환한다.")
    void save_duplicate_request() {
        //given
        CreateChatRoomResponse response = chatRoomService.save(TranslateLanguage.KOREAN, "tae@google", "other@google");

        //when
        CreateChatRoomResponse createChatRoomResponse = chatRoomService.save(TranslateLanguage.KOREAN, "tae@google",
                "other@google");
        //then
        Assertions.assertThat(response.chatRoomId()).isEqualTo(createChatRoomResponse.chatRoomId());
    }
    @Test
    @DisplayName("동일한 회원 사이에 다른 언어로 채팅방 요청이 들어오면, 새로운 채팅방을 생성한다.")
    void save_other_request() {
        //given
        CreateChatRoomResponse response = chatRoomService.save(TranslateLanguage.KOREAN, "tae@google", "other@google");

        //when
        CreateChatRoomResponse createChatRoomResponse = chatRoomService.save(TranslateLanguage.ENGLISH, "tae@google",
                "other@google");
        //then
        Assertions.assertThat(response.chatRoomId()).isNotEqualTo(createChatRoomResponse.chatRoomId());
    }
}