package com.wootech.transtalk.repository;

import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.enums.TranslateLanguage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    @Query("""
            SELECT cr 
            FROM ChatRoom cr
            JOIN cr.participants p
            JOIN cr.participants p2
            WHERE p.user.id = :userId
            AND (:name IS NULL OR p2.user.name LIKE CONCAT('%',:name,'%'))
            ORDER BY cr.lastMessageTime DESC NULLS LAST
            """)
    Page<ChatRoom> findChatRoomsByParticipantIdAndName(@Param("userId") Long userId,
                                                       @Param("name") String name,
                                                       Pageable pageable);

    @Query("""
            SELECT cr 
            FROM ChatRoom cr
            JOIN cr.participants p1
            JOIN cr.participants p2
            WHERE p1.user.id = :userIdA
            AND p2.user.id = :userIdB
            AND cr.language = :language
            """)
    Optional<ChatRoom> findChatRoomBetweenUsers(@Param("userIdA") Long userIdA,
                                                @Param("userIdB") Long userIdB,
                                                @Param("language")TranslateLanguage language);
}
