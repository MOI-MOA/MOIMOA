package com.b110.jjeonchongmu.domain.schedule.dto;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private Timestamp scheduleStartTime;
    private Long perBudget;
    private Long totalBudget;
    private Date penaltyApplyDate;

    //createDTO와 updateDTO 추가
    private Long scheduleId;
    private Long gatheringId;
    private Long subManagerId;
    private Integer scheduleStatus;


}