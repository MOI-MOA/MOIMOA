package com.b110.jjeonchongmu.domain.schedule.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleCreateDTO {
    private String scheduleTitle;//
    private String scheduleDetail;//
    private String schedulePlace;//
    private LocalDateTime penaltyApplyDate; //
    private int penaltyRate;
    private LocalDateTime scheduleStartTime; //
    private String scheduleAccountPw;
    private Long subManagerId;
    private Long perBudget;//

    public void updateSubManagerId(Long id) {
        this.subManagerId = id;
    }
    public void updateScheduleDate(LocalDateTime scheduleStartTime, LocalDateTime penaltyApplyDate) {
        this.penaltyApplyDate = penaltyApplyDate;
        this.scheduleStartTime = scheduleStartTime;
    }
}