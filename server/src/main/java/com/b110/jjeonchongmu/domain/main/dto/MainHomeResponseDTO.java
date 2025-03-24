package com.b110.jjeonchongmu.domain.main.dto;

import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 메인 홈 화면 응답 DTO
 * - 사용자의 모임 및 일정 정보를 포함
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainHomeResponseDTO {
    
    private int uncheckScheduleCount;
    private List<DateDTO> dateList;
    private List<ScheduleListDTO> todayScheduleList;
    private List<ScheduleListDTO> upcommingScheduleList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DateDTO {
        private int date;

        public static List<DateDTO> fromSchedules(List<Schedule> schedules) {
            // 현재 월의 일정들만 필터링
            LocalDateTime now = LocalDateTime.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();

            return schedules.stream()
                    .filter(schedule -> {
                        if (schedule.getStartTime() == null) return false;
                        LocalDateTime scheduleTime = schedule.getStartTime().toLocalDate();
                        return scheduleTime.getMonthValue() == currentMonth && 
                               scheduleTime.getYear() == currentYear;
                    })
                    .map(schedule -> {
                        LocalDateTime scheduleTime = schedule.getStartTime().toLocalDate();
                        return DateDTO.builder()
                                .date(scheduleTime.getDayOfMonth())
                                .build();
                    })
                    .distinct()  // 중복된 날짜 제거
                    .sorted(Comparator.comparingInt(DateDTO::getDate))  // 날짜순 정렬
                    .collect(Collectors.toList());
        }
    }

//    @Builder
//    public MainHomeResponseDTO(int uncheckScheduleCount, List<DateDTO> dateList,
//                             List<ScheduleResponseDTO> todayScheduleList, List<ScheduleResponseDTO> upcommingScheduleList) {
//        this.uncheckScheduleCount = uncheckScheduleCount;
//        this.dateList = dateList;
//        this.todayScheduleList = todayScheduleList;
//        this.upcommingScheduleList = upcommingScheduleList;
//    }
} 