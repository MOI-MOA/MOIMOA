package com.b110.jjeonchongmu.domain.schedule.dto;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ScheduleDTO {
    private Long gatheringId;
    private String gatheringName;
    private Long scheduleId;
    private String scheduleTitle;
    private String schedulePlace;
    private LocalDateTime scheduleStartTime;
    private Long perBudget;
    private Integer attendeeCount;

    public static ScheduleDTO from(Schedule schedule) {
        return ScheduleDTO.builder()
                .gatheringId(schedule.getGathering().getGatheringId())
                .gatheringName(schedule.getGathering().getGatheringName())
                .scheduleId(schedule.getId())
                .scheduleTitle(schedule.getTitle())
                .schedulePlace(schedule.getPlace())
                .scheduleStartTime(schedule.getStartTime())
                .perBudget(schedule.getPerBudget())
                .attendeeCount(schedule.getAttendees().size())
                .build();
    }
}
