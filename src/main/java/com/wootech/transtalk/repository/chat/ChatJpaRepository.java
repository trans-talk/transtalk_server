package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.entity.Chat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatJpaRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findTopByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    Optional<Chat> findTopBySenderIdAndChatRoomIdOrderByCreatedAtDesc(Long senderId, Long chatRoomId);
}
