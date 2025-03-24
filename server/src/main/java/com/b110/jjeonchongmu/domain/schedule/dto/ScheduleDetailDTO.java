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
    // 참여자 몇명인지만 파악하고 참여자 리스트는 클릭했을때 보여주기

}