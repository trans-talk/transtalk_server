package com.wootech.transtalk.repository;

import com.wootech.transtalk.entity.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    List<ChatRoom> findByParticipantsUserId(Long userId);
}
