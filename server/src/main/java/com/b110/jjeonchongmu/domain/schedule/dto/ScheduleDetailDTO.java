
package com.b110.jjeonchongmu.domain.schedule.dto;
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
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
    private LocalDateTime scheduleStartTime;
    //perBudget 없음.
    private int totalBudget;
    //패널티 없음.

    private int scheduleId;
    private int gatheringId;
    //부총무 정보 없음.
    //스케줄 상태 없음. ddd

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