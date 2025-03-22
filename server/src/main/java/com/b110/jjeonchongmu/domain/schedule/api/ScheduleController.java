package com.b110.jjeonchongmu.domain.schedule.api;

import com.b110.jjeonchongmu.domain.schedule.dto.*;
import com.b110.jjeonchongmu.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleListDto>> getScheduleList() {
        return ResponseEntity.ok(scheduleService.getScheduleList());
    }

    @PostMapping
    public ResponseEntity<String> createSchedule(@RequestBody ScheduleCreateDto dto) {
        scheduleService.createSchedule(dto);
        return ResponseEntity.status(201).body("일정이 생성되었습니다.");
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetail(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getScheduleDetail(scheduleId));
    }

    @PatchMapping
    public ResponseEntity<String> updateSchedule(@RequestBody ScheduleUpdateDto dto) {
        scheduleService.updateSchedule(dto);
        return ResponseEntity.status(204).body("일정이 수정되었습니다.");
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.status(204).body("일정이 삭제되었습니다.");
    }

    @PostMapping("/{scheduleId}/attend")
    public ResponseEntity<String> attendSchedule(@PathVariable Long scheduleId) {
        scheduleService.attendSchedule(scheduleId);
        return ResponseEntity.status(201).body("일정 참석이 완료되었습니다.");
    }

    @DeleteMapping("/{scheduleId}/cancel")
    public ResponseEntity<String> cancelAttendance(@PathVariable Long scheduleId) {
        scheduleService.cancelAttendance(scheduleId);
        return ResponseEntity.status(204).body("일정 참석이 취소되었습니다.");
    }

    @GetMapping("/{scheduleId}/amount")
    public ResponseEntity<PerBudgetDto> getPerBudget(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getPerBudget(scheduleId));
    }
}
