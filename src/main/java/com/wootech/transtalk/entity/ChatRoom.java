package com.wootech.transtalk.entity;

import com.wootech.transtalk.enums.TranslateLanguage;
import com.wootech.transtalk.exception.custom.NotFoundException;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.util.ArrayList;
import java.util.List;

import static com.wootech.transtalk.exception.ErrorMessages.PARTICIPANT_NOT_FOUND_ERROR;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TranslateLanguage language;
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.PERSIST)
    private List<Participant> participants = new ArrayList<>();

    public ChatRoom(TranslateLanguage language) {
        this.language = language;
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    public User getRecipient(Long currentUserId) {
        return participants.stream().filter(participant -> !participant.getUser().getId().equals(currentUserId))
                .map(Participant::getUser).findFirst()
                .orElseThrow(() -> new NotFoundException(PARTICIPANT_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404)));
    }

    public Instant getMyLastReadTime(Long currentUserId) {
        return participants.stream().filter(participant -> participant.getUser().getId().equals(currentUserId))
                .map(Participant::getLastReadTime).findFirst()
                .orElseThrow(() -> new NotFoundException(PARTICIPANT_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404)));
    }

    public void exit(Long currentUserId) {
        Participant me = participants.stream()
                .filter(participant -> participant.getUser().getId().equals(currentUserId)).findFirst()
                .orElseThrow(() -> new NotFoundException(PARTICIPANT_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404)));
        me.markAsExited();
    }
}
