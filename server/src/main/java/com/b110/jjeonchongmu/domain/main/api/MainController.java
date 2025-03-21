package com.b110.jjeonchongmu.domain.main.api;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.service.MainService;
import com.b110.jjeonchongmu.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainController {

    private final MainService mainService;

    /**
     * 메인 화면 정보 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getMainInfo() {
        MainResponseDTO response = mainService.getMainInfo();
        return ResponseEntity.ok(new ApiResponse(200, "메인 화면 정보 조회 성공", response));
    }

    /**
     * 미확인 일정 목록 조회
     */
    @GetMapping("/schedule/uncheck")
    public ResponseEntity<ApiResponse> getUncheckSchedules() {
        List<ScheduleDTO> response = mainService.getUncheckSchedules();
        return ResponseEntity.ok(new ApiResponse(200, "미확인 일정 목록 조회 성공", response));
    }

    /**
     * 개인 일정 목록 조회
     */
    @GetMapping("/schedule/personal")
    public ResponseEntity<ApiResponse> getPersonalSchedules() {
        List<ScheduleDTO> response = mainService.getPersonalSchedules();
        return ResponseEntity.ok(new ApiResponse(201, "개인 일정 목록 조회 성공", response));
    }

    /**
     * 해당 달 일정 조회
     */
    @GetMapping("/schedule/{year}/{month}")
    public ResponseEntity<ApiResponse> getMonthSchedules(
            @PathVariable int year,
            @PathVariable int month) {
        MonthScheduleDTO response = mainService.getMonthSchedules(year, month);
        return ResponseEntity.ok(new ApiResponse(201, "월간 일정 조회 성공", response));
    }

    /**
     * 해당 일 일정 조회
     */
    @GetMapping("/schedule/{year}/{month}/{date}")
    public ResponseEntity<ApiResponse> getDaySchedules(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int date) {
        DayScheduleDTO response = mainService.getDaySchedules(year, month, date);
        return ResponseEntity.ok(new ApiResponse(201, "일간 일정 조회 성공", response));
    }

    /**
     * 모임 일정 목록 조회
     */
    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse> getGatheringSchedules() {
        List<ScheduleDTO> response = mainService.getGatheringSchedules();
        return ResponseEntity.ok(new ApiResponse(201, "모임 일정 목록 조회 성공", response));
    }

    /**
     * 오늘 일정 조회
     */
    @GetMapping("/schedule/today")
    public ResponseEntity<ApiResponse> getTodaySchedules() {
        List<ScheduleDTO> response = mainService.getTodaySchedules();
        return ResponseEntity.ok(new ApiResponse(201, "오늘 일정 조회 성공", response));
    }
} 