package com.wootech.transtalk.repository;

import com.wootech.transtalk.entity.Chat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findTopByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
