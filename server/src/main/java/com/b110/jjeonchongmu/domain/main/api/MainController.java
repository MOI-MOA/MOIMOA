package com.b110.jjeonchongmu.domain.main.api;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.service.MainService;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDTO;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        if (token == null || token.isEmpty()) {
            return 1L; // 테스트용 기본 사용자 ID
        }
        String jwtToken = token.replace("Bearer ", "");
        return Long.valueOf(jwtTokenProvider.getUserId(jwtToken));
    }
//    @GetMapping("/test")
//    public String test() {
//        return "API is working!";
//    }

    /**
     *  1. 홈 화면 조회 - GET /api/v1/main
     *    - 미확인 일정 개수 조회
     *    - 이번달 일정 조회
     *    - 다가오는 일정 조회
     */
    @GetMapping
    public ResponseEntity<MainHomeResponseDTO> getMainHome(@RequestHeader(value = "Authorization", required = false) String token) {
        System.out.println("Controller token" + token);
        try {
            Long userId = userIdFromToken(token);
            MainHomeResponseDTO response = mainService.getMainHome(userId);
            System.out.println("Controller response: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Controller exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    /**
     * 2. 미확인 일정목록 조회 - GET /api/v1/main/schedule/uncheck
     */
    @GetMapping("/schedule/uncheck")
    public ResponseEntity<List<ScheduleDTO>> getUncheckSchedules(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = userIdFromToken(token);
            List<ScheduleDTO> response = mainService.getUncheckSchedules(userId);
            System.out.println("Controller getUncheckSchedules response: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Controller getUncheckSchedules exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 3. 개인 일정목록 조회 - GET /api/v1/main/schedule/personal
     */
    @GetMapping("/schedule/personal")
    public ResponseEntity<List<ScheduleDTO>> getPersonalSchedules(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = userIdFromToken(token);
            List<ScheduleDTO> response = mainService.getPersonalSchedules(userId);
            System.out.println("Controller getPersonalSchedules response: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Controller getPersonalSchedules exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 4. 해당 달 일정 조회 - GET /api/v1/main/schedule/{year}/{month}
     */
    @GetMapping("/schedule/{year}/{month}")
    public ResponseEntity<List<DateDTO>> getMonthSchedules(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable int year,
            @PathVariable int month) {

        try {
            Long userId = userIdFromToken(token);
            List<DateDTO> response = mainService.getMonthSchedules(userId, year, month);
            System.out.println("Controller getMonthSchedules response: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Controller getMonthSchedules exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 5. 특정 날짜 일정 조회 - GET /api/v1/main/schedule/{year}/{month}/{date}
     */
    @GetMapping("/schedule/{year}/{month}/{date}")
    public ResponseEntity<List<ScheduleDTO>> getDaySchedules(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int date) {
        try {
            Long userId = userIdFromToken(token);
            List<ScheduleDTO> response = mainService.getDaySchedules(userId, year, month, date);
            System.out.println("Controller getDaySchedules response: " + response);
            return ResponseEntity.ok(response);

        }catch (Exception e) {
            System.out.println("Controller getDaySchedules exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 6. 오늘 일정 조회 - GET /api/v1/main/schedule/today
     */
    @GetMapping("/schedule/today")
    public ResponseEntity<List<ScheduleDTO>> getTodaySchedules(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = userIdFromToken(token);
            List<ScheduleDTO> response = mainService.getTodaySchedules(userId);
            System.out.println("Controller getTodaySchedules response: " + response);
            return ResponseEntity.ok(response);

        }catch (Exception e) {
            System.out.println("Controller getTodaySchedules exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}