package com.wootech.transtalk.event.user;

import com.wootech.transtalk.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomCleanupEventListener {

    private final ChatRoomService chatRoomService;
    private final ApplicationEventPublisher publisher;

    @TransactionalEventListener
    public void handleChatRoomCleanup(ChatRoomCleanupEvent event) {
        Long chatRoomId = event.getChatRoomId();
        log.info("[EventListener] ChatRoomCleanupEvent 수신: 채팅방 ID = {}", chatRoomId);

        // 채팅방이 soft하게 삭제되었는가
        boolean isRoomSoftDeleted = chatRoomService.cleanupRoom(chatRoomId);

        // 삭제됨 -> chat message 삭제 이벤트 발행
        if (isRoomSoftDeleted) {
            log.info("[EventListener] Softly Deleted ChatRoom ID={}", chatRoomId);
            publisher.publishEvent(new ChatMessageCleanupEvent(chatRoomId));
        } else {
            log.info("[EventListener] There is Valid Participants in ChatRoom ID={}", chatRoomId);
        }
    }
}