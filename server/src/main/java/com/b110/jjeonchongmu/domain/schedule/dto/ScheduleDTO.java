package com.b110.jjeonchongmu.domain.schedule.dto;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private boolean isSubManager;

    public static ScheduleDTO from(Schedule schedule) {
        return ScheduleDTO.builder()
                .gatheringId(schedule.getGathering().getGatheringId())
                .gatheringName(schedule.getGathering().getGatheringName())
                .scheduleId(schedule.getId())
                .scheduleTitle(schedule.getTitle())
                .schedulePlace(schedule.getPlace())
                .scheduleStartTime(schedule.getStartTime().plusHours(9))
                .perBudget(schedule.getPerBudget())
                .attendeeCount(schedule.getAttendees().size())
                .build();
    }

    public void updateIsSubManager(boolean isSubManager){this.isSubManager = isSubManager;}
}
