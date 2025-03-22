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
public class ScheduleDetailDto {
    private int gatheringId;
    private String gatheringName;
    private int scheduleId;
    private String scheduleTitle;
    private String scheduleDetail;
    private ManagerInfo scheduleManager;
    private LocalDateTime scheduleStartTime;
    private String schedulePlace;
    private int attendCount;
    private int totalBudget;
    private boolean scheduleAttendStatus;
    private List<AttendeeInfo> attendees;

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