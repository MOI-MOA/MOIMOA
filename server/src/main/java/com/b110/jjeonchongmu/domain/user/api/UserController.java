package com.b110.jjeonchongmu.domain.user.api;

import com.b110.jjeonchongmu.domain.user.dto.request.LoginRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.request.PasswordChangeRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.request.SignupRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.response.TokenResponseDTO;
import com.b110.jjeonchongmu.domain.user.dto.response.UserResponseDTO;
import com.b110.jjeonchongmu.domain.user.service.UserService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 API
     * 새로운 사용자를 등록합니다.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDTO request) {
        userService.signup(request);
        return ResponseEntity.status(200).body("회원가입 성공");
    }

    /**
     * 로그인 API
     * 이메일과 비밀번호로 로그인하고 토큰을 발급받습니다.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        TokenResponseDTO tokenResponse = userService.login(request);
        return ResponseEntity.status(200).body(tokenResponse);
    }

    /**
     * 로그아웃 API
     * 현재 사용 중인 토큰을 무효화합니다.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        userService.logout();
        return ResponseEntity.status(200).body("로그아웃 성공");
    }

    /**
     * 사용자 정보 조회 API
     * 현재 로그인한 사용자의 정보를 조회합니다.
     */
    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDTO> getMyInfo() {
        long userId = jwtTokenProvider.getUserId();
        UserResponseDTO response = userService.getMyInfo(userId);
        return ResponseEntity.status(200).body(response);
    }

    /**
     * 비밀번호 변경 API
     * 현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.
     */
    @PatchMapping("/user/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequestDTO request) {
        long userId = jwtTokenProvider.getUserId();
        userService.changePassword(request, userId);
        return ResponseEntity.status(200).body("비밀번호 변경 성공");
    }

    /**
     * 회원 탈퇴 API
     * 사용자 정보를 삭제합니다.
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw() {
        userService.withdraw();
        return ResponseEntity.status(200).body("회원 탈퇴 성공");
    }
}
