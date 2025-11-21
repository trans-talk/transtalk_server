package com.wootech.transtalk.service.user;

import com.wootech.transtalk.dto.auth.AuthSignInResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.ChatRoomRepository;
import com.wootech.transtalk.repository.ParticipantRepository;
import com.wootech.transtalk.repository.chat.ChatJpaRepository;
import com.wootech.transtalk.repository.chat.ChatRepositoryMongoAdapter;
import com.wootech.transtalk.repository.user.UserRepository;
import com.wootech.transtalk.service.auth.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wootech.transtalk.exception.ErrorMessages.USER_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RefreshTokenService refreshTokenService;
    private final ChatJpaRepository chatJpaRepository;
    private final ChatRepositoryMongoAdapter chatRepositoryMongoAdapter;

    // 사용자 프로필 조회
    public AuthSignInResponse.UserResponse getProfileById(AuthUser authUser) {
        User foundUser = getUserById(authUser.getUserId());

        return AuthSignInResponse.UserResponse.builder()
                .id(foundUser.getId())
                .email(foundUser.getEmail())
                .name(foundUser.getName())
                .picture(foundUser.getPicture())
                .build();
    }

    // 회원가입
    public User findByEmailOrGet(String email, String name, UserRole userRole, String picture) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .userRole(userRole)
                                .picture(picture)
                                .build()
                ));
    }

    // 사용자 조회
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[AuthService] Received User Id={}", id);
                    return new NotFoundException(USER_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404));
                });
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[AuthService] Received User Email={}", email);
                    return new NotFoundException(USER_NOT_FOUND_ERROR, HttpStatusCode.valueOf(404));
                });
    }

    // 회원탈퇴
    @Transactional
    public void withdrawUser(AuthUser authUser) {
        Long userId = authUser.getUserId();
        log.info("[UserService] Received User ID={}", userId);

        User user = getUserById(userId);

        refreshTokenService.deleteAllTokensByUserId(userId);
        log.info("[UserService] Try To Delete Refresh Token With User ID={}", userId);

        // User 삭제  -> @SQL delete
        userRepository.deleteById(userId);
        log.info("[UserService] User ID={} Soft Deleted", userId);

        // Participant 삭제
        List<Participant> participantsToDelete = participantRepository.findByUser(user);
        // 변경될 수 있는 채팅방
        Set<ChatRoom> ChatRooms = new HashSet<>();

        for (Participant participant : participantsToDelete) {
            participantRepository.deleteById(participant.getId());
            ChatRooms.add(participant.getChatRoom());
        }
        log.info("[UserService] All Participants({}) Soft Deleted: By User ID={}", participantsToDelete.size(), userId);

        // 채팅방 삭제
        for (ChatRoom chatRoom : ChatRooms) {
            // 제거되지 않고 남은 participant 가져오기
            List<Participant> remainingActiveParticipants = participantRepository.findByChatRoom(chatRoom);

            // 참여자 없을 경우 채팅방 삭제
            if (remainingActiveParticipants.isEmpty()) {
                chatRoomRepository.deleteById(chatRoom.getId());
                log.info("[UserService] ALl Participants Have Withdrawn With Chat Room ID={}", chatRoom.getId());
            }
            // 채팅 삭제
            chatJpaRepository.deleteByChatRoomId(chatRoom.getId());
        }

        log.info("[UserService] User Withdrawal Process Completed With User ID={}", userId);
    }
}
