package com.wootech.transtalk.repository;

import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.entity.ParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {
}
