package com.b110.jjeonchongmu.domain.main.api;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.service.MainService;
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
//    private final JwtTokenProvider jwtTokenProvider;

    /**
     *  1. 홈 화면 조회 - GET /api/v1/home
     *    getMainHome()
     *    - 미확인 일정 개수 조회
     *    - 이번달 일정 조회
     *    - 다가오는 일정 조회
     */
//    @GetMapping
//    public ResponseEntity<MainHomeResponse> getMainHome(@RequestHeader("Authorization") String token) {
//        // 1. "Bearer " 접두사 제거
//        String jwtToken = token.replace("Bearer ", "");
//
//        // 2. JWT 토큰에서 사용자 정보 추출
//        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
//
//        // 3. 사용자 정보를 포함하여 서비스 메서드 호출
//        return ResponseEntity.ok(mainService.getMainHome(userId));
//    }


    /**2. 미확인 일정목록 조회 - GET /api/v1/main/schdule/uncheck*/
    @GetMapping("/schedule/uncheck")
    public ResponseEntity<ScheduleListResponse> getUncheckSchedules() {
        return ResponseEntity.ok(mainService.getUncheckSchedules());
    }

    /**3. 개인 일정목록 조회 - GET /api/v1/main/schedule/personal*/
    @GetMapping("/schedule/personal")
    public ResponseEntity<ScheduleListResponse> getPersonalSchedules() {
        return ResponseEntity.ok(mainService.getPersonalSchedules());
    }

    /**4. 해당 달 일정 조회 - GET /api/v1/main/schedule/{year}/{month}*/
    @GetMapping("/schedule/{year}/{month}")
    public ResponseEntity<MonthlyScheduleResponse> getMonthlySchedules(
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(mainService.getMonthlySchedules(year, month));
    }

    /**5.  해당 일 일정 조회 - GET /api/v1/main/schedule/{year}/{month}/{date}*/
    @GetMapping("/schedule/{year}/{month}/{date}")
    public ResponseEntity<ScheduleListResponse> getDailySchedules(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int date) {
        return ResponseEntity.ok(mainService.getDailySchedules(year, month, date));
    }

    /**2. 오늘 일정 조회 - GET /api/v1/main/schedule/today*/
    @GetMapping("/schedule/today")
    public ResponseEntity<ScheduleListResponse> getTodaySchedules() {
        return ResponseEntity.ok(mainService.getTodaySchedules());
    }
}
