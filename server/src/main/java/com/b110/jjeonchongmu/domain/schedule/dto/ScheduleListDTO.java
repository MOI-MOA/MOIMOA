package com.b110.jjeonchongmu.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleListDTO {
    List<ScheduleDetailDTO> scheduleList;
    private int gatheringId;
    private String gatheringName;
    private int scheduleId;
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private LocalDateTime scheduleStartTime;
    private long perBudget;
    private long totalBudget;
    private LocalDate penaltyApplyDate;
    private int scheduleStatus;
    private int attendeeCount;
} 