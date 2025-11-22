package com.wootech.transtalk.config;

import com.wootech.transtalk.domain.ChatMessage;
import com.wootech.transtalk.dto.chatroom.CreateChatRoomResponse;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslateLanguage;
import com.wootech.transtalk.enums.TranslationStatus;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.repository.chat.ChatRepository;
import com.wootech.transtalk.repository.user.UserRepository;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

//@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final ChatRepository chatRepository;

    @PostConstruct
    public void init() {
        //기본 유저 생성
        for (int i = 1; i <= 10; i++) {
            userRepository.save(User.builder()
                    .name("test" + i)
                    .picture("image" + i)
                    .email("test" + i + "@email.com")
                    .userRole(UserRole.ROLE_USER)
                    .build());
        }

        List<String> userNames = List.of("유찬영", "이호수", "유태선");
        List<String> userEmails = List.of("yoocy01@gmail.com", "lakevely27@gmail.com", "yts950184@gmail.com");
        for (int i = 0; i < userEmails.size(); i++) {
            User owner = createUser(userNames.get(i), userEmails.get(i));
            //채팅방 추가
            CreateChatRoomResponse savedChatRoom = chatRoomService.save(TranslateLanguage.ENGLISH, owner.getEmail(),
                    "test1@email.com");
            //채팅 추가
            createChat(owner.getId(), owner.getEmail(), savedChatRoom.chatRoomId());
        }
    }

    private User createUser(String userName, String userEmail) {
        return userRepository.save(User.builder()
                .name(userName)
                .picture("image")
                .email(userEmail)
                .userRole(UserRole.ROLE_USER)
                .build());
    }

    private void createChat(Long ownerId, String ownerEmail, Long chatRoomId) {
        List<String> korean = List.of("안녕?", "반가워", "오늘 하루 어땠어?", "아주 좋아!", "영화 보러 갈래?", "좋아, 어떤 영화?");
        List<String> english = List.of("Hi?", "Nice to see you", "How was your day?", "Great!",
                "Want to go see a movie?", "Sure, what movie?");

        for (int i = 0; i < korean.size(); i++) {
            if (i % 2 == 0) {
                chatRepository.save(new ChatMessage(null, korean.get(i), english.get(i), chatRoomId,
                        ownerId, ownerEmail, true, Instant.now(), TranslationStatus.COMPLETED));
            } else if (i % 2 == 1) {
                chatRepository.save(new ChatMessage(null, korean.get(i), english.get(i), chatRoomId,
                        1L, "test1@email.com", true, Instant.now(), TranslationStatus.COMPLETED));
            }
        }
    }
}