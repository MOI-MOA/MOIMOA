package com.b110.jjeonchongmu.domain.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDto {
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