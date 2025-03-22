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
public class ScheduleUpdateDto {
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private LocalDateTime scheduleStartDate;
    private LocalDateTime scheduleEndDate;
    private LocalDateTime scheduleDate;
    private int totalBudget;
    private LocalDateTime penaltyApplyDate;
} 