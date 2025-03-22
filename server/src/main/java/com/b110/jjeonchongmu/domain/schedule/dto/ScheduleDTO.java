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
    private Long scheduleId;
    private Long groupId;
    private Long subManagerId;
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private Timestamp scheduleStartTime;
    private Long perBudget;
    private Long totalBudget;
    private Date penaltyApplyDate;
    private Integer scheduleStatus;


}