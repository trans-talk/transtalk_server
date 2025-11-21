package com.wootech.transtalk.repository.user;

import com.wootech.transtalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@Param("email")String email);
    // 탈퇴한 (deletedAt != null) 사용자들의 ID만 조회하는 쿼리
    @Query("SELECT u.id FROM User u WHERE u.deletedAt IS NOT NULL")
    List<Long> findSoftDeletedUserIds();
}
