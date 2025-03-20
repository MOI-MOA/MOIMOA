package com.b110.jjeonchongmu.domain.home.api;

/**
 * 홈 관련 API 컨트롤러
 * 
 * 1. 홈 화면 조회 - GET /api/v1/home
 *    - 미확인 일정 조회
 *    - 이번달 일정 조회
 *    - 다가오는 일정 조회
 *    Response: unconfirmedSchedules, monthlySchedules, upcomingSchedules
 */
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {
    // TODO: 구현 예정
} 