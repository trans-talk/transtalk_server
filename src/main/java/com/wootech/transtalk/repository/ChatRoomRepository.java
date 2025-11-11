package com.wootech.transtalk.repository;

import com.wootech.transtalk.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Page<ChatRoom> findByParticipantsUserId(Long userId, Pageable pageable);
}
