package com.wootech.transtalk.service.participant;

import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.repository.ParticipantRepository;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final UserService userService;
    private final ParticipantRepository participantRepository;

    @Transactional
    public List<Long> softDeleteParticipantsByUserId(Long userId) {

        List<Participant> participants = participantRepository.findByUserId(userId);

        for (Participant participant : participants) {
            participantRepository.deleteById(participant.getId());
        }
        log.info("[ParticipantService] All Participants Softly Deleted By User ID={}, size={}", userId, participants.size());

        // 영향받은 chat room 리스트들 반환
        return participants.stream()
                .map(p -> p.getChatRoom().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Participant> findParticipantsByChatRoom(Long chatRoomId){
       return participantRepository.findByChatRoomId(chatRoomId);
    }
}
