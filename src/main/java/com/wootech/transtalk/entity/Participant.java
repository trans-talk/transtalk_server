package com.wootech.transtalk.entity;


import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@SQLDelete(sql = "UPDATE participant SET deleted_at = NOW() WHERE participant_id = ?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column
    private Instant lastReadTime;

    public Participant(User user, ChatRoom chatRoom) {
        this.user = user;
        joinChatRoom(chatRoom);
        lastReadTime = Instant.now();
    }

    private void joinChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        chatRoom.addParticipant(this);
    }

    public void markAsExited() {
        Instant now = Instant.now();
        validateLastReadTime(now);
        this.lastReadTime = now;
    }

    private void validateLastReadTime(Instant now) {
        if (this.lastReadTime.isAfter(now)) {
            throw new RuntimeException("");
        }
    }

}
