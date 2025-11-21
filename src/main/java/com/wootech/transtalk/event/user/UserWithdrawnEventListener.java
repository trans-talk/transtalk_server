package com.wootech.transtalk.event.user;

import com.wootech.transtalk.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserWithdrawnEventListener {

    private final ParticipantService participantService;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행자 주입

    // Transaction이 성공적으로 커밋된 후에 이 이벤트를 처리
    @TransactionalEventListener
    public void handleUserWithdraw(UserWithdrawnEvent event) {
        Long userId = event.getUserId();
        log.info("[EventListener] UserWithdrawnEvent Published: User ID={}", userId);

        // 참여자 소프트 딜리트 및 관련된 채팅방 ID 목록 반환
        List<Long> affectedChatRoomIds = participantService.softDeleteParticipantsByUserId(userId);
        log.info("[EventListener] Softly Deleted User ID={} Affected Participants(counts={}), Chat Rooms(counts={})", userId, affectedChatRoomIds.size(), affectedChatRoomIds.size());

        // 채팅방들에 삭제 이벤트 발행
        affectedChatRoomIds.forEach(chatRoomId ->
                eventPublisher.publishEvent(new ChatRoomCleanupEvent(chatRoomId))
        );
        log.info("[EventListener] ChatRoomCleanupEvent Published: Counts={}", affectedChatRoomIds.size());
    }
}