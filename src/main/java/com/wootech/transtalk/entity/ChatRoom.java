package com.wootech.transtalk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;
    @Column(nullable = false)
    private String language;
    @OneToMany(mappedBy = "chatRoom")
    private List<Participant> participants = new ArrayList<>();

    public ChatRoom(String language) {
        this.language = language;
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }
}
