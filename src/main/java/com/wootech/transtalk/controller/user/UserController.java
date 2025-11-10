package com.wootech.transtalk.controller.user;

import com.wootech.transtalk.dto.ApiResponse;
import com.wootech.transtalk.dto.auth.AuthSignInResponse;
import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 프로필 가져오기
    @GetMapping("/me")
    public ApiResponse<AuthSignInResponse.UserResponse> getMyInfo(@AuthenticationPrincipal AuthUser authUser) {
        return ApiResponse.success(userService.getProfileById(authUser), "회원 정보 조회에 성공했습니다.");
    }
}
