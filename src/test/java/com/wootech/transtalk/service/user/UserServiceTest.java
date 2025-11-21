package com.wootech.transtalk.service.user;

import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.entity.Chat;
import com.wootech.transtalk.entity.ChatRoom;
import com.wootech.transtalk.entity.Participant;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.enums.TranslateLanguage;
import com.wootech.transtalk.enums.UserRole;
import com.wootech.transtalk.repository.ChatRoomRepository;
import com.wootech.transtalk.repository.ParticipantRepository;
import com.wootech.transtalk.repository.chat.ChatJpaRepository;
import com.wootech.transtalk.repository.user.UserRepository;
import com.wootech.transtalk.service.auth.RefreshTokenService;
import com.wootech.transtalk.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*; // Mockito 스태틱 임포트

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatJpaRepository chatJpaRepository;

    @InjectMocks // @Mock으로 선언된 객체들을 자동으로 주입해줍니다. 생성자가 없어도 동작해요!
    private UserService userService;


    @Test
    void 사용자가_회원탈퇴시_관련된_participant와_chatroom_모두를_soft_delete_한다() {
        // given
        Long userId = 10L;
        AuthUser authUser = new AuthUser(userId, "test@test.com", "name", UserRole.ROLE_USER);

        User mockUser = User.builder()
                .id(userId)
                .email("test@test.com")
                .build();

        ChatRoom chatRoom1 = Mockito.spy(new ChatRoom(TranslateLanguage.ENGLISH));
        ChatRoom chatRoom2 = Mockito.spy(new ChatRoom(TranslateLanguage.KOREAN));

        doReturn(100L).when(chatRoom1).getId();

        Participant p1 = Mockito.spy(new Participant(mockUser, chatRoom1));
        Participant p2 = Mockito.spy(new Participant(mockUser, chatRoom2));

        doReturn(1L).when(p1).getId();
        doReturn(2L).when(p2).getId();

        List<Participant> participants = List.of(p1, p2);

        // 사용자 레포에서 사용자 id 로 조회 가능
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(participantRepository.findByUser(mockUser)).thenReturn(participants);

        // room1 → 삭제된 유저들
        when(participantRepository.findByChatRoom(chatRoom1))
                .thenReturn(List.of());

        // room2 유저있음
        Participant remain = new Participant(User.builder().id(99L).build(), chatRoom2);
        when(participantRepository.findByChatRoom(chatRoom2))
                .thenReturn(List.of(remain));

        // when
        // 유저 삭제
        userService.withdrawUser(authUser);

        // then
        // 관련 refresh 토큰 모두 삭제
        verify(refreshTokenService).deleteAllTokensByUserId(userId);
        // 해당 유저가 삭제됨
        verify(userRepository).deleteById(userId);

        // 해당 유저가 있었던 participant 삭제
        verify(participantRepository).deleteById(1L);
        verify(participantRepository).deleteById(2L);

        // 해당 유저가 있었던 채팅방만 삭제
        verify(chatRoomRepository).deleteById(100L);
        verify(chatRoom2, never()).softDelete();
    }


    // 추가 테스트 케이스: 유저가 참여한 채팅방이 하나도 없는 경우
    @Test
    void 사용자가_참여한_채팅방이_없을때_채팅방과_참여자_데이터는_삭제되지않는다() {
        // Given
        Long userId = 10L;
        AuthUser authUser = new AuthUser(userId, "test@example.com", "TESTER", UserRole.ROLE_USER);

        User testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .name("TestUser")
                .userRole(UserRole.ROLE_USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(participantRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        // When
        userService.withdrawUser(authUser);

        // Then
        verify(refreshTokenService, times(1)).deleteAllTokensByUserId(userId);
        verify(userRepository, times(1)).deleteById(userId);
        verify(participantRepository, times(1)).findByUser(testUser);

        // participant가 없으므로 deleteById 호출 안됨
        verify(participantRepository, never()).deleteById(anyLong());
        // chatRoom 관련 메서드도 호출 안됨
        verify(participantRepository, never()).findByChatRoom(any(ChatRoom.class));
        verify(chatRoomRepository, never()).deleteById(anyLong());
    }
}