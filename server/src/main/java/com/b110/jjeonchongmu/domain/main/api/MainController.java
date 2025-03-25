package com.b110.jjeonchongmu.domain.main.api;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.service.MainService;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDTO;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDTO;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 홈 관련 API 컨트롤러
 *
 *  1. 홈 화면 조회 - GET /api/v1/main
 *    - 미확인 일정 조회
 *    - 이번달 일정 조회
 *    - 다가오는 일정 조회
 *    Response: unconfirmedSchedules, monthlySchedules, upcomingSchedules
 *  2. 미확인 일정목록 조회 - GET /api/v1/main/schedule/uncheck
 *  3. 개인 일정목록 조회 - GET /api/v1/main/schedule/personal
 *  4. 이번달 일정 조회 - GET /api/v1/main/schedule/{year}/{month}
 *  5. 특정 날짜 일정 조회 - GET /api/v1/main/schedule/{year}/{month}/{date}
 *  6. 오늘 일정 조회 - GET /api/v1/main/schedule/today
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainController {

    private final MainService mainService;
    private final JwtTokenProvider jwtTokenProvider;

    private Long userIdFromToken(String token) {
        String jwtToken = token.replace("Bearer ", "");
        return Long.valueOf(jwtTokenProvider.getUserId(jwtToken));
    }

    /**
     *  1. 홈 화면 조회 - GET /api/v1/main
     *    - 미확인 일정 개수 조회
     *    - 이번달 일정 조회
     *    - 다가오는 일정 조회
     */
    @GetMapping
    public ResponseEntity<MainHomeResponseDTO> getMainHome(@RequestHeader("Authorization") String token) {
        Long userId = userIdFromToken(token);
        MainHomeResponseDTO response = mainService.getMainHome(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. 미확인 일정목록 조회 - GET /api/v1/main/schedule/uncheck
     */
    @GetMapping("/schedule/uncheck")
    public ResponseEntity<List<ScheduleDTO>> getUncheckSchedules(@RequestHeader("Authorization") String token) {
        Long userId = userIdFromToken(token);
        List<ScheduleDTO> response = mainService.getUncheckSchedules(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. 개인 일정목록 조회 - GET /api/v1/main/schedule/personal
     */
    @GetMapping("/schedule/personal")
    public ResponseEntity<List<ScheduleDTO>> getPersonalSchedules(@RequestHeader("Authorization") String token) {
        Long userId = userIdFromToken(token);
        List<ScheduleDTO> response = mainService.getPersonalSchedules(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 4. 해당 달 일정 조회 - GET /api/v1/main/schedule/{year}/{month}
     */
    @GetMapping("/schedule/{year}/{month}")
    public ResponseEntity<List<DateDTO>> getMonthSchedules(
            @RequestHeader("Authorization") String token,
            @PathVariable int year,
            @PathVariable int month) {
        Long userId = userIdFromToken(token);
        List<DateDTO> response = mainService.getMonthSchedules(userId, year, month);
        return ResponseEntity.ok(response);
    }

    /**
     * 5. 특정 날짜 일정 조회 - GET /api/v1/main/schedule/{year}/{month}/{date}
     */
    @GetMapping("/schedule/{year}/{month}/{date}")
    public ResponseEntity<List<ScheduleDTO>> getDaySchedules(
            @RequestHeader("Authorization") String token,
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int date) {
        Long userId = userIdFromToken(token);
        List<ScheduleDTO> response = mainService.getDaySchedules(userId, year, month, date);
        return ResponseEntity.ok(response);
    }

    /**
     * 6. 오늘 일정 조회 - GET /api/v1/main/schedule/today
     */
    @GetMapping("/schedule/today")
    public ResponseEntity<List<ScheduleDTO>> getTodaySchedules(@RequestHeader("Authorization") String token) {
        Long userId = userIdFromToken(token);
        List<ScheduleDTO> response = mainService.getTodaySchedules(userId);
        return ResponseEntity.ok(response);
    }
}