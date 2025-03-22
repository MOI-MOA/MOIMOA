package com.b110.jjeonchongmu.domain.main.service;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.repo.MainRepo;
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
    public MainHomeResponseDTO getMainHome() {
        // 오늘의 일정과 다가오는 일정 조회
        List<ScheduleDTO> todaySchedules = mainRepo.findTodaySchedules().stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());
        
        List<ScheduleDTO> upcomingSchedules = mainRepo.findUpcomingSchedules().stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());

        return MainHomeResponseDTO.builder()
                .schedules(todaySchedules)
                .build();
    }

    /**
     * 미확인 일정 목록 조회
     */
    public ScheduleListResponseDTO getUncheckSchedules() {
        List<ScheduleDTO> schedules = mainRepo.findUncheckSchedules().stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());

        return ScheduleListResponseDTO.builder()
                .schedules(schedules)
                .build();
    }

    /**
     * 개인 일정 목록 조회
     */
    public ScheduleListResponseDTO getPersonalSchedules() {
        List<ScheduleDTO> schedules = mainRepo.findPersonalSchedules().stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());

        return ScheduleListResponseDTO.builder()
                .schedules(schedules)
                .build();
    }

    /**
     * 월별 일정 조회
     */
    public MonthlyScheduleResponseDTO getMonthlySchedules(int year, int month) {
        List<ScheduleDTO> schedules = mainRepo.findSchedulesByYearAndMonth(year, month).stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());

        return MonthlyScheduleResponseDTO.builder()
                .year(year)
                .month(month)
                .schedules(schedules)
                .build();
    }

    /**
     * 특정 날짜의 일정 조회
     */
    public ScheduleListResponseDTO getDailySchedules(int year, int month, int date) {
        LocalDate targetDate = LocalDate.of(year, month, date);
        List<ScheduleDTO> schedules = mainRepo.findSchedulesByDate(targetDate).stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());

        return ScheduleListResponseDTO.builder()
                .schedules(schedules)
                .build();
    }

    /**
     * 오늘의 일정 조회
     */
    public ScheduleListResponseDTO getTodaySchedules() {
        List<ScheduleDTO> schedules = mainRepo.findTodaySchedules().stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());

        return ScheduleListResponseDTO.builder()
                .schedules(schedules)
                .build();
    }
}
