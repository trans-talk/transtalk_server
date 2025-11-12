package com.wootech.transtalk.service.chat;

import com.wootech.transtalk.dto.ChatMessageRequest;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.entity.ChatContent;
import com.wootech.transtalk.repository.chat.ChatMongoRepository;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatContentService {

    private final ChatMongoRepository chatMongoRepository;
    private final UserService userService;

    public ChatContent saveChat(AuthUser authUser, ChatMessageRequest request, Long chatRoomId) {
        userService.getUserById(authUser.getUserId());

        // TODO: 채팅방 유효성 검증 로직
        log.info("[ChatContentService] Received ChatRoom ID={}", chatRoomId);

         return chatMongoRepository.save(
                ChatContent.builder()
                        .originalContent(request.content())
                        .translatedContent("translated")
                        .senderId(authUser.getUserId())
                        .chatroomId(chatRoomId)
                        .build()
        );
    }
}
