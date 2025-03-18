package com.b110.jjeonchongmu.domain.schedule.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private Long scheduleId;
    private Long groupId;
    private Long subManagerId;
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private LocalDateTime scheduleStartTime;
    private Long perBudget;
    private Long totalBudget;
    private LocalDateTime penaltyApplyDate;
    private Integer scheduleStatus;
}