package com.b110.jjeonchongmu.domain.schedule.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequiredArgsConstructor;

/**
 * 일정 관련 API 컨트롤러
 * 
 * [총무 권한]
 * 1. 일정 생성 - POST /api/v1/schedule
 * 2. 일정 수정 - PATCH /api/v1/schedule
 * 3. 일정 삭제 - DELETE /api/v1/schedule/{scheduleId}
 * 
 * [모든 사용자]
 * 4. 개인 일정목록 조회 - GET /api/v1/main/schedule/personal
 * 5. 이번달 일정 조회 - GET /api/v1/main/schedule/month
 * 6. 모임 일정목록 조회 - GET /api/v1/main/schedule/{gatheringId}/gathering
 * 7. 오늘 일정 조회 - GET /api/v1/main/schedule/day
 * 8. 일정 상세조회 - GET /api/v1/schedule/{scheduleId}
 * 9. 일정 참석 - POST /api/v1/schedule/{scheduleId}/attend
 * 10. 일정 참석 취소 - POST /api/v1/schedule/{scheduleId}/cancel
 * 11. 일정인당금액확인 - GET /api/v1/schedule/{scheduleId}/amount
 */
@RestController
@RequiredArgsConstructor
public class ScheduleController {
    // TODO: 구현 예정
}
