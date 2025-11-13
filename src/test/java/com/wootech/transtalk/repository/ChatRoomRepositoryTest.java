package com.wootech.transtalk.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wootech.transtalk.controller.chatroom.ChatRoomController;
import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.dto.chatroom.ChatRoomListResponse;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslateLanguage;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class ChatRoomRepositoryTest {
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomController controller;

    @Test
    void a() throws JsonProcessingException {
        User user = User.builder().name("tae").email("tae@email").picture("image1").userRole(UserRole.ROLE_USER)
                .build();
        User user2 = User.builder().name("seon").email("seon@email").picture("image2").userRole(UserRole.ROLE_USER)
                .build();
        User user3 = User.builder().name("bong").email("bong@email").picture("image3").userRole(UserRole.ROLE_USER)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);

        ChatRoom chatRoom = new ChatRoom(TranslateLanguage.KOREAN);
        new Participant(user, chatRoom);
        new Participant(user2, chatRoom);
        chatRoomRepository.save(chatRoom);

        ChatRoom chatRoom1 = new ChatRoom(TranslateLanguage.KOREAN);
        new Participant(user, chatRoom1);
        new Participant(user3, chatRoom1);
        chatRoomRepository.save(chatRoom1);

        Chat chat = new Chat("hello", user, chatRoom);
        Chat chat1 = new Chat("hihi", user2, chatRoom);
        Chat save = chatRepository.save(chat);
        chatRepository.save(chat1);

        Chat chat2 = new Chat("ss", user, chatRoom1);
        Chat chat3 = new Chat("okay", user3, chatRoom1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        ApiResponse<ChatRoomListResponse> tae = controller.findChatRooms(
                new AuthUser(1L, "tae@email", "tae", UserRole.ROLE_USER),
                PageRequest.of(0, 20));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Instant 등 지원
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tae);
        System.out.println(json);
    }

}