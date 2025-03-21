package com.b110.jjeonchongmu.domain.main.service;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {
    private final ScheduleRepo scheduleRepo;
    private final JwtUtil jwtUtil;

    /**
     * 메인 화면 정보 조회
     */
    public MainResponseDTO getMainInfo() {
        Long userId = jwtUtil.getCurrentMemberId();
        LocalDate today = LocalDate.now();

        return MainResponseDTO.builder()
                .uncheckScheduleCount(scheduleRepo.countUncheckSchedules(userId))
                .dateList(getDateList(today.getYear(), today.getMonthValue()))
                .todayScheduleList(getTodaySchedules())
                .upcommingScheduleList(getUpcomingSchedules())
                .build();
    }

    /**
     * 미확인 일정 목록 조회
     */
    public List<ScheduleDTO> getUncheckSchedules() {
        Long userId = jwtUtil.getCurrentMemberId();
        List<Schedule> schedules = scheduleRepo.findUncheckSchedules(userId);
        return convertToScheduleDTOs(schedules);
    }

    /**
     * 개인 일정 목록 조회
     */
    public List<ScheduleDTO> getPersonalSchedules() {
        Long userId = jwtUtil.getCurrentMemberId();
        List<Schedule> schedules = scheduleRepo.findPersonalSchedules(userId);
        return convertToScheduleDTOs(schedules);
    }

    /**
     * 해당 달 일정 조회
     */
    public MonthScheduleDTO getMonthSchedules(int year, int month) {
        return MonthScheduleDTO.builder()
                .dates(getDateList(year, month))
                .build();
    }

    /**
     * 해당 일 일정 조회
     */
    public DayScheduleDTO getDaySchedules(int year, int month, int date) {
        Long userId = jwtUtil.getCurrentMemberId();
        LocalDateTime startOfDay = LocalDateTime.of(year, month, date, 0, 0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Schedule> schedules = scheduleRepo.findByDateBetween(userId, startOfDay, endOfDay);
        return DayScheduleDTO.builder()
                .schedules(convertToScheduleDTOs(schedules))
                .build();
    }

    /**
     * 모임 일정 목록 조회
     */
    public List<ScheduleDTO> getGatheringSchedules() {
        Long userId = jwtUtil.getCurrentMemberId();
        List<Schedule> schedules = scheduleRepo.findGatheringSchedules(userId);
        return convertToScheduleDTOs(schedules);
    }

    /**
     * 오늘 일정 조회
     */
    public List<ScheduleDTO> getTodaySchedules() {
        Long userId = jwtUtil.getCurrentMemberId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Schedule> schedules = scheduleRepo.findByDateBetween(userId, startOfDay, endOfDay);
        return convertToScheduleDTOs(schedules);
    }

    private List<DateDTO> getDateList(int year, int month) {
        return IntStream.rangeClosed(1, LocalDate.of(year, month, 1).lengthOfMonth())
                .mapToObj(day -> DateDTO.builder().date(day).build())
                .collect(Collectors.toList());
    }

    private List<ScheduleDTO> convertToScheduleDTOs(List<Schedule> schedules) {
        return schedules.stream()
                .map(schedule -> ScheduleDTO.builder()
                        .gatheringId(schedule.getGathering().getGatheringId())
                        .gatheringName(schedule.getGathering().getGatheringName())
                        .scheduleId(schedule.getScheduleId())
                        .scheduleTitle(schedule.getScheduleTitle())
                        .scheduleDetail(schedule.getScheduleDetail())
                        .schedulePlace(schedule.getSchedulePlace())
                        .scheduleStartTime(schedule.getScheduleStartTime())
                        .perBudget(schedule.getPerBudget())
                        .totalBudget(schedule.getTotalBudget())
                        .penaltyApplyDate(schedule.getPenaltyApplyDate())
                        .scheduleStatus(schedule.getScheduleStatus())
                        .attendeeCount(schedule.getAttendeeCount())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ScheduleDTO> getUpcomingSchedules() {
        Long userId = jwtUtil.getCurrentMemberId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthLater = now.plusMonths(1);

        List<Schedule> schedules = scheduleRepo.findByDateBetween(userId, now, oneMonthLater);
        return convertToScheduleDTOs(schedules);
    }
}
