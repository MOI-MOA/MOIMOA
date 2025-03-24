package com.b110.jjeonchongmu.domain.schedule.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ScheduleListDTO {
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
    private Integer scheduleStatus;
    private Integer attendeeCount;
}
