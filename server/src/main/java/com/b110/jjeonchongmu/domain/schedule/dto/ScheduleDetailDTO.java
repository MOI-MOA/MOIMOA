
package com.b110.jjeonchongmu.domain.schedule.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDetailDTO {
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private LocalDateTime scheduleStartTime;
    private Long perBudget;
    private Long totalBudget;
    private Date penaltyApplyDate;
    private Date penaltyRate;
    private Long scheduleId;
    private Long gatheringId;
    private Long scheduleSubManagerId;

    //모임장 정보 추가
    private ManagerInfo scheduleManager;
    //참석자 정보 추가
    private String gatheringName;
    private List<AttendeeInfo> attendees;
    private int attendCount;
    private boolean scheduleAttendStatus;

    @Getter
    @Builder
    public static class ManagerInfo {

        private long userId;
        private String username;
    }

    @Getter
    @Builder
    public static class AttendeeInfo {
        private long userId;
        private String username;
    }
}