package com.wootech.transtalk.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Long lastReadChatId = 0L;

    public Participant(User user, ChatRoom chatRoom) {
        this.user = user;
        joinChatRoom(chatRoom);
    }

    private void joinChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        chatRoom.addParticipant(this);
    }

    public void markAsExited(Long lastReadChatId) {
        if (this.lastReadChatId >= lastReadChatId) {
            throw new RuntimeException("");
        }
        this.lastReadChatId = lastReadChatId;
    }

}
