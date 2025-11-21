package com.wootech.transtalk.repository;

import com.wootech.transtalk.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByUserId(Long userId);

    List<Participant> findByChatRoomId(Long chatRoomId);
}
