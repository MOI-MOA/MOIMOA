package com.b110.jjeonchongmu.domain.main.service;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.repo.MainRepo;
import com.b110.jjeonchongmu.domain.schedule.dto.*;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 메인 화면 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {
    private final MainRepo mainRepo;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 메인 홈 화면 정보 조회
     * - 미확인 일정 수
     * - 오늘의 일정
     * - 다가오는 일정
     */
//    public MainHomeResponseDTO getMainHome(Long userId) {
//        // 오늘의 일정과 다가오는 일정 조회
//        List<ScheduleListDTO> todaySchedules = mainRepo.findTodaySchedules().stream()
//                .map(ScheduleListDTO::from)
//                .collect(Collectors.toList());
//
//        List<ScheduleListDTO> upcomingSchedules = mainRepo.findUpcomingSchedules().stream()
//                .map(ScheduleListDTO::from)
//                .collect(Collectors.toList());
//
//        return MainHomeResponseDTO.builder()
//                .schedules(todaySchedules)
//                .build();
//    }
//
//    /**
//     * 미확인 일정 목록 조회
//     */
//    public ScheduleListDTO getUncheckSchedules(Long userId) {
//        List<ScheduleListDTO> schedules = mainRepo.findUncheckSchedules().stream()
//                .map(ScheduleListDTO::from)
//                .collect(Collectors.toList());
//
//        return ScheduleListDTO.builder()
//                .schedules(schedules)
//                .build();
//    }
//
//    /**
//     * 개인 일정 목록 조회
//     */
//    public ScheduleListDTO getPersonalSchedules(Long userId) {
//        List<ScheduleDTO> schedules = mainRepo.findPersonalSchedules().stream()
//                .map(ScheduleDTO::from)
//                .collect(Collectors.toList());
//
//        return ScheduleListDTO.builder()
//                .schedules(schedules)
//                .build();
//    }
//
//    /**
//     * 월별 일정 조회
//     * /api/v1/main/schedule/{year}/{month}
//     *
//     */
//    public MonthScheduleDTO getMonthSchedules(int year, int month) {
//        // 해당 월에 일정이 있는 날짜들을 조회
//        List<LocalDateTime> scheduleDates = mainRepo.findSchedulesByYearAndMonth(year, month);
//
//        // 날짜만 추출하여 DateInfo 리스트로 변환
//        List<MonthScheduleDTO.DateInfo> dates = scheduleDates.stream()
//                .map(date -> MonthScheduleDTO.DateInfo.builder()
//                        .date(date.getDayOfMonth())
//                        .build())
//                .distinct()
//                .sorted()
//                .collect(Collectors.toList());
//
//        return MonthScheduleDTO.builder()
//                .datas(dates)
//                .build();
//    }
//
//    /**
//     * 특정 날짜의 일정 조회 수정 필요. userId는 jwt에서 가져오도록.
//     */
//    public DayScheduleDTO getDaySchedules(Long userId,    int year, int month, int date) {
//        LocalDate targetDate = LocalDate.of(year, month, date);
//        List<ScheduleDTO> schedules = mainRepo.findSchedulesByDate(targetDate).stream()
//                .map(schedule -> ScheduleDTO.builder()
//                        .gatheringId(schedule.getGathering().getGatheringId())
//                        .gatheringName(schedule.getGathering().getGatheringName())
//                        .scheduleId(schedule.getScheduleId())
//                        .scheduleTitle(schedule.getScheduleTitle())
//                        .scheduleDetail(schedule.getScheduleDetail())
//                        .schedulePlace(schedule.getSchedulePlace())
//                        .scheduleStartTime(schedule.getScheduleStartTime())
//                        .perBudget(schedule.getPerBudget())
//                        .totalBudget(schedule.getTotalBudget())
//                        .penaltyApplyDate(schedule.getPenaltyApplyDate())
//                        .scheduleStatus(schedule.getScheduleStatus())
//                        .attendeeCount(schedule.getScheduleMembers().size())
//                        .build())
//                .collect(Collectors.toList());
//
//        return DayScheduleDTO.builder()
//                .datas(schedules)
//                .build();
//    }
//
//    /**
//     * 오늘의 일정 조회
//     */
//    public ScheduleListResponseDTO getTodaySchedules() {
//        List<ScheduleDTO> schedules = mainRepo.findTodaySchedules().stream()
//                .map(ScheduleDTO::from)
//                .collect(Collectors.toList());
//
//        return ScheduleListResponseDTO.builder()
//                .schedules(schedules)
//                .build();
//    }
}
