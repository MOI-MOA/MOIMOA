package com.b110.jjeonchongmu.domain.main.api;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.service.MainService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
// 스케줄에서 import 해서 쓰기. ㅎ
/**
 * 홈 관련 API 컨트롤러
 * 
 *  1. 홈 화면 조회 - GET /api/v1/main
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
    private final JwtTokenProvider jwtTokenProvider;

    private Long UserIdFromToken(String token) {
        String jwtToken = token.replace("Bearer ", "");
        return Long.valueOf(jwtTokenProvider.getUserId(jwtToken));
    }

    /**
     *  1. 홈 화면 조회 - GET /api/v1/main
     *    getMainHome()
     *    - 미확인 일정 개수 조회
     *    - 이번달 일정 조회
     *    - 다가오는 일정 조회
     */
   @GetMapping
   public ResponseEntity<MainHomeResponseDTO> getMainHome(@RequestHeader("Authorization") String token) {
       Long userId = UserIdFromToken(token);
       MainHomeResponseDTO response = mainService.getMainHome(userId);
       return ResponseEntity.ok(response);
   }


    /**
     * 2. 미확인 일정목록 조회 - GET /api/v1/main/schdule/uncheck
     */
    @GetMapping("/schedule/uncheck")
    public ResponseEntity<List<ScheduleListDTO>> getUncheckSchedules(@RequestHeader("Authorization") String token) {
        Long userId = UserIdFromToken(token);
        List<ScheduleListDTO> response = mainService.getUncheckSchedules(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. 개인 일정목록 조회 - GET /api/v1/main/schedule/personal
     *
     * */
    @GetMapping("/schedule/personal")
    public ResponseEntity<List<ScheduleListDTO>> getPersonalSchedules(@RequestHeader("Authorization") String token) {
        Long userId = UserIdFromToken(token);
        List<ScheduleListDTO> response = mainService.getPersonalSchedules(userId);
        return ResponseEntity.ok(response);
    }

    /**4. 해당 달 일정 조회 - GET /api/v1/main/schedule/{year}/{month}*/
    @GetMapping("/schedule/{year}/{month}")
    public ResponseEntity<List<DateDTO>> getMonthSchedules(
            @RequestHeader("Authorization") String token,
            @PathVariable int year,
            @PathVariable int month) {
        Long userId = UserIdFromToken(token);
        List<DateDTO> response = mainService.getMonthSchedules(userId, year, month);
        return ResponseEntity.ok(response);
    }

    /**5.  해당 일 일정 조회 - GET /api/v1/main/schedule/{year}/{month}/{date}*/
    @GetMapping("/schedule/{year}/{month}/{date}")
    public ResponseEntity<List<ScheduleListDTO>> getDaySchedules(
            @RequestHeader("Authorization") String token,
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int date) {
        Long userId = UserIdFromToken(token);
        List<ScheduleListDTO> response = mainService.getDaySchedules(userId, year, month, date);
        return ResponseEntity.ok(response);
    }

    /**6. 오늘 일정 조회 - GET /api/v1/main/schedule/today*/
    @GetMapping("/schedule/today")
    public ResponseEntity<List<ScheduleListDTO>> getTodaySchedules(@RequestHeader("Authorization") String token) {
        Long userId = UserIdFromToken(token);
        List<ScheduleListDTO> response = mainService.getTodaySchedules(userId);
        return ResponseEntity.ok(response);
    }
}
