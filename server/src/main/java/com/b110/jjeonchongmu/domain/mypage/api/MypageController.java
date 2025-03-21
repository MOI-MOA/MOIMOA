package com.b110.jjeonchongmu.domain.mypage.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 관련 API 컨트롤러
 * 
 * 1. 마이페이지 조회 - GET /api/v1/mypage
 *    - Response: userId, name, email, scheduleNotificationEnabled
 * 
 * 2. 통계 현황 조회 - GET /api/v1/mypage/statistics
 *    - Response: 
 *      - 총 지출액
 *      - 월평균 지출액
 *      - 최다 지출 모임
 *      - 최다 지출 월
 *      - 평균 참석률
 *      - 최다 참석 모임
 */
@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MypageController {
    // TODO: 구현 예정
}
