package com.b110.jjeonchongmu.domain.schedule.api;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 일정 관련 API 컨트롤러
 * [총무 권한]
 * 1. 일정 생성 - POST /api/v1/schedule
 * 2. 일정 수정 - PATCH /api/v1/schedule
 * 3. 일정 삭제 - DELETE /api/v1/schedule/{scheduleId}
 * [모든 사용자]
 * 4. 일정 상세조회 - GET /api/v1/schedule/{scheduleId}
 * 5. 일정 참석 - POST /api/v1/schedule/{scheduleId}/attend
 * 6. 일정 참석 취소 - POST /api/v1/schedule/{scheduleId}/cancel
 * 7. 일정인당금액확인 - GET /api/v1/schedule/{scheduleId}/amount
 */
@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
@Builder
public class ScheduleController {
    // TODO: 구현 예정
}
