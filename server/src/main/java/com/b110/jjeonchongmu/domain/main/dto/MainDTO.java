package com.b110.jjeonchongmu.domain.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainResponseDTO {
    private int uncheckScheduleCount;
    private List<DateDTO> dateList;
    private List<ScheduleDTO> todayScheduleList;
    private List<ScheduleDTO> upcommingScheduleList;
}

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DateDTO {
    private int date;
}

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ScheduleDTO {
    private Long gatheringId;
    private String gatheringName;
    private Long scheduleId;
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private LocalDateTime scheduleStartTime;
    private Long perBudget;
    private Long totalBudget;
    private LocalDateTime penaltyApplyDate;
    private int scheduleStatus; // 0:대기, 1:진행중, 2:종료
    private int attendeeCount;
}

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MonthScheduleDTO {
    private List<DateDTO> dates;
}

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DayScheduleDTO {
    private List<ScheduleDTO> schedules;
} 