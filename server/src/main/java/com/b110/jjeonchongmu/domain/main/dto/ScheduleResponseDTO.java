package com.b110.jjeonchongmu.domain.schedule.dto;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponseDTO {
    private Long gatheringId;
    private String gatheringName;
    private Long scheduleId;
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private Timestamp scheduleStartTime;
    private Long perBudget;
    private Long totalBudget;
    private Date penaltyApplyDate;
    private int scheduleStatus;  // 0:대기, 1:진행중, 2:종료
    private int attendeeCount;

    public static ScheduleResponseDTO from(Schedule schedule) {
        return ScheduleResponseDTO.builder()
                .gatheringId(schedule.getGathering().getGatheringId())
                .gatheringName(schedule.getGathering().getGatheringName())
                .scheduleId(schedule.getId())
                .scheduleTitle(schedule.getTitle())
                .scheduleDetail(schedule.getDetail())
                .schedulePlace(schedule.getPlace())
                .scheduleStartTime(schedule.getStartTime())
                .perBudget(schedule.getPerBudget())
                .totalBudget(schedule.getTotalBudget())
                .penaltyApplyDate(schedule.getPenaltyApplyDate())
                .scheduleStatus(schedule.getStatus())
                .attendeeCount(schedule.getAttendees().size())
                .build();
    }
} 