package com.wootech.transtalk.event;

import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.service.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class MessageNotificationEventHandler {
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(MessageNotificationEvent event) {
        ChatRoom findChatRoom = chatRoomService.findById(event.getChatRoomId());
        User recipient = findChatRoom.getRecipient(event.getCurrentUserId());

        ChatRoomResponse response = chatRoomService.updateChatRoomInfo(event.getChatRoomId(), recipient.getId());
        messagingTemplate.convertAndSend(
                "/topic/users/" + recipient.getId() + "/chatRoom",
                response
        );
    }
}
