package com.b110.jjeonchongmu.domain.schedule.api;

import com.b110.jjeonchongmu.domain.account.dto.MakeAccountDTO;
import com.b110.jjeonchongmu.domain.account.service.ScheduleAccountService;
import com.b110.jjeonchongmu.domain.schedule.dto.*;
import com.b110.jjeonchongmu.domain.schedule.service.ScheduleMemberService;
import com.b110.jjeonchongmu.domain.schedule.service.ScheduleService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
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
    private final ScheduleMemberService scheduleMemberService;
    private final ScheduleAccountService scheduleAccountService;
    private final JwtTokenProvider jwtTokenProvider;
    // 모임 일정목록 조회
    @GetMapping("/{gatheringId}")
    public ResponseEntity<List<ScheduleDTO>> getScheduleList(@PathVariable Long gatheringId ) {

        Long userId = jwtTokenProvider.getUserId();
//        Long userId = 1L;
        return ResponseEntity.status(200).body(scheduleService.getScheduleList(userId,gatheringId));
    }
    // 일정 상세조회
    @GetMapping("/{scheduleId}/detail")
    public ResponseEntity<ScheduleDetailDTO> getScheduleDetail(@PathVariable Long scheduleId) {
        Long userId = jwtTokenProvider.getUserId();
//        Long userId = 1L;
        return ResponseEntity.status(200).body(scheduleService.getScheduleDetail(userId,scheduleId));
    }
    // 일정 생성(총무만)
    @PostMapping({"/{gatheringId}"})
    public ResponseEntity<String> createSchedule(@RequestBody ScheduleCreateDTO scheduleCreateDTO, @PathVariable Long gatheringId) {
        Long userId = jwtTokenProvider.getUserId();
//        Long userId = 5L;
        Long scheduleId = scheduleService.createSchedule(userId,gatheringId,scheduleCreateDTO);

            MakeAccountDTO makeAccountDTO = MakeAccountDTO.builder().accountPw(scheduleCreateDTO.getScheduleAccountPw()).build();

        scheduleAccountService.createAccount(userId,scheduleId,makeAccountDTO,scheduleCreateDTO.getPerBudget());

        System.out.println("일정 번호" + scheduleId);
        scheduleMemberService.setSubManager(gatheringId,scheduleId,scheduleCreateDTO.getSubManagerId(),scheduleCreateDTO.getPerBudget());
        return ResponseEntity.status(201).body("일정이 생성되었습니다.");
    }
    // 일정 수정(총무만)
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<String> updateSchedule(@RequestBody ScheduleUpdateDTO scheduleUpdateDTO, @PathVariable Long scheduleId) {

//        Long userId = jwtTokenProvider.getUserId();
        Long userId = 1L ;

        scheduleService.updateSchedule(userId,scheduleId,scheduleUpdateDTO);
        return ResponseEntity.status(204).body("일정이 수정되었습니다.");
    }
    // 일정 삭제(총무만)
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId) {

        Long userId = jwtTokenProvider.getUserId();
//        Long userId = 5L;

        // 일정멤버와 일정계좌 일정을 전부다 삭제
        scheduleService.deleteSchedule(userId,scheduleId);

        return ResponseEntity.status(204).body("일정이 삭제되었습니다.");
    }
    // 일정 멤버(참여자) 목록 조회
    @GetMapping({"/{scheduleId}/member"})
    public ResponseEntity<List<ScheduleMemberDTO>> getScheduleMember(@PathVariable Long scheduleId){
        Long userId = jwtTokenProvider.getUserId();
//        Long userId = 1L;
        return ResponseEntity.status(200).body(scheduleMemberService.getScheduleMember(userId,scheduleId));
    }
    // 일정 참석
    @PostMapping("/{scheduleId}/attend")
    public ResponseEntity<String> attendSchedule(@PathVariable Long scheduleId) {
        Long userId = jwtTokenProvider.getUserId();
//        Long userId = 4L;
        scheduleMemberService.attendSchedule(userId,scheduleId);
        return ResponseEntity.status(200).body("일정 참석이 완료되었습니다.");
    }
    // 일정 참석 취소
    @DeleteMapping("/{scheduleId}/cancel")
    public ResponseEntity<String> cancelAttendance(@PathVariable Long scheduleId) {
        Long userId = jwtTokenProvider.getUserId();
//        Long userId = 4L;
        scheduleMemberService.cancelAttendance(userId,scheduleId);
        return ResponseEntity.status(200).body("일정 참석이 취소되었습니다.");
    }
}
