package com.wootech.transtalk.event.user;

import com.wootech.transtalk.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageCleanupEventListener {

    private final ChatService chatService; // MongoDB Chat 메시지 처리를 위해

    @TransactionalEventListener
    public void handleChatMessageCleanup(ChatMessageCleanupEvent event) {
        Long chatRoomId = event.getChatRoomId();
        log.info("[EventListener] ChatMessageCleanupEvent Chat Room ID={}", chatRoomId);

        // mongo DB 삭제가 필요하다면 로직 작성
    }
}