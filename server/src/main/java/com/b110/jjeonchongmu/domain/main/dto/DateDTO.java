package com.b110.jjeonchongmu.domain.main.dto;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 일정 날짜 정보 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateDTO {
    private int date;

    /**
     * 스케줄 목록에서 날짜 정보만 추출하여 DateDTO 리스트 생성
     */
    public static List<DateDTO> fromSchedules(List<Schedule> schedules) {
        // 현재 월의 일정들만 필터링
        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        return schedules.stream()
                .filter(schedule -> {
                    if (schedule.getStartTime() == null) return false;
                    LocalDateTime scheduleTime = schedule.getStartTime();
                    return scheduleTime.getMonthValue() == currentMonth &&
                            scheduleTime.getYear() == currentYear;
                })
                .map(schedule -> {
                    LocalDateTime scheduleTime = schedule.getStartTime();
                    return DateDTO.builder()
                            .date(scheduleTime.getDayOfMonth())
                            .build();
                })
                .distinct()  // 중복된 날짜 제거
                .sorted(Comparator.comparingInt(DateDTO::getDate))  // 날짜순 정렬
                .collect(Collectors.toList());
    }

    /**
     * 특정 년월의 일정 날짜 정보만 추출
     */
    public static List<DateDTO> fromSchedulesByYearMonth(List<Schedule> schedules, int year, int month) {
        return schedules.stream()
                .filter(schedule -> {
                    if (schedule.getStartTime() == null) return false;
                    LocalDateTime scheduleTime = schedule.getStartTime();
                    return scheduleTime.getMonthValue() == month &&
                            scheduleTime.getYear() == year;
                })
                .map(schedule -> {
                    LocalDateTime scheduleTime = schedule.getStartTime();
                    return DateDTO.builder()
                            .date(scheduleTime.getDayOfMonth())
                            .build();
                })
                .distinct()  // 중복된 날짜 제거
                .sorted(Comparator.comparingInt(DateDTO::getDate))  // 날짜순 정렬
                .collect(Collectors.toList());
    }
}