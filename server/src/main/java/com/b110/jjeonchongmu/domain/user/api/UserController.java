package com.b110.jjeonchongmu.domain.user.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

/**
 * 회원 관련 API 컨트롤러
 * 
 * 1. 로그인 - POST /api/v1/login
 *    - Request: name, email, password, pin
 *    - Response: refreshToken, accessToken
 * 
 * 2. 로그아웃 - POST /api/v1/logout
 *
 * 3. 회원가입 - POST /api/v1/signup
 *    - Request: username, email, password, personalAccountPW
 *    - 계좌생성. 
 * 
 * 4. 회원탈퇴
 * 
 * 5. 비밀번호 변경
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    // TODO: 구현 예정
}
