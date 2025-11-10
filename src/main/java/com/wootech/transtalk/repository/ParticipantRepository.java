package com.wootech.transtalk.repository;

import com.wootech.transtalk.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
