package com.b110.jjeonchongmu.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleCreateDTO {
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private LocalDateTime scheduleStartTime;
    private long perBudget;
    private long totalBudget;
    private LocalDateTime penaltyApplyDate;
} 