package com.b110.jjeonchongmu.domain.user.api;

import com.b110.jjeonchongmu.domain.user.dto.LoginRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.SignupRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.request.LoginRequest;
import com.b110.jjeonchongmu.domain.user.dto.request.PasswordChangeRequest;
import com.b110.jjeonchongmu.domain.user.dto.request.SignupRequest;
import com.b110.jjeonchongmu.domain.user.dto.response.UserResponse;
import com.b110.jjeonchongmu.domain.user.dto.response.TokenResponse;
import com.b110.jjeonchongmu.domain.user.service.UserService;
import com.b110.jjeonchongmu.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * 새로운 사용자를 등록합니다.
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequestDTO request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 로그인 API
     * 이메일, 비밀번호, PIN으로 로그인하고 토큰을 발급받습니다.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequestDTO request) {
        TokenResponse tokenResponse = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    /**
     * 로그아웃 API
     * 현재 사용 중인 토큰을 무효화합니다.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        userService.logout();
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 사용자 정보 조회 API
     * 현재 로그인한 사용자의 정보를 조회합니다.
     */
    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        UserResponse response = userService.getMyPage();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 비밀번호 변경 API
     * 현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.
     */
    @PatchMapping("/user/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 회원 탈퇴 API
     * 사용자 정보를 삭제합니다.
     */
    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse<Void>> withdraw() {
        userService.withdraw();
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 알림 설정 변경 API
     * 일정 알림 설정을 토글합니다.
     */
    @PatchMapping("/user/notification")
    public ResponseEntity<ApiResponse<Void>> toggleNotification() {
        userService.toggleNotification();
        return ResponseEntity.ok(ApiResponse.success());
    }
}
