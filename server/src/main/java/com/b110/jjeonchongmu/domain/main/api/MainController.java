package com.b110.jjeonchongmu.domain.main.api;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 홈 관련 API 컨트롤러
 * 
 *  1. 홈 화면 조회 - GET /api/v1/home
 *    - 미확인 일정 조회
 *    - 이번달 일정 조회
 *    - 다가오는 일정 조회
 *    Response: unconfirmedSchedules, monthlySchedules, upcomingSchedules
 *  2. 개인 일정목록 조회 - GET /api/v1/main/schedule/personal
 *  3. 이번달 일정 조회 - GET /api/v1/main/schedule/month/{year}/{month}
 *  4. 모임 일정목록 조회 - GET /api/v1/main/schedule/{gatheringId}/gathering
 *  5. 오늘 일정 조회 - GET /api/v1/main/schedule/day
 *
 *
 */


@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
@Builder
public class MainController {
    // TODO: 구현 예정
} 