package com.b110.jjeonchongmu.domain.schedule.dto;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.user.entity.User;
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
public class ScheduleDetailDTO {
    private Long gatheringId;
    private String gatheringName;
    private Long scheduleId;
    private String scheduleTitle;
    private String subManagerName;
    private LocalDateTime scheduleStartTime;
    private String schedulePlace;
    private Long perBudget;
    private String scheduleDetail;
    private int attendeeCount;
    private boolean isSubManager;
    private Long scheduleAccountBalance;

    // 참여자 몇명인지만 파악하고 참여자 리스트는 클릭했을때 보여주기

    public static ScheduleDetailDTO from(Schedule schedule){
        int size = 0;
        for (ScheduleMember sm : schedule.getAttendees()) {
            if (sm.getScheduleIsCheck() && sm.getIsAttend()) {
                size++;
            }
        }
        ScheduleDetailDTO scheduleDetailDTO = ScheduleDetailDTO.builder()
                .gatheringId(schedule.getGathering().getGatheringId())
                .gatheringName(schedule.getGathering().getGatheringName())
                .scheduleId(schedule.getId())
                .scheduleTitle(schedule.getTitle())
                .subManagerName(schedule.getSubManager().getName())
                .scheduleStartTime(schedule.getStartTime())
                .schedulePlace(schedule.getTitle())
                .perBudget(schedule.getPerBudget())
                .scheduleDetail(schedule.getDetail())
                .attendeeCount(size)
                .build();
        return scheduleDetailDTO;
    }
    public void updateIsSubManger(boolean isSubManager){this.isSubManager = isSubManager;}
    public void updateScheduleAccountBalance(Long scheduleAccountBalance){this.scheduleAccountBalance = scheduleAccountBalance;}


}