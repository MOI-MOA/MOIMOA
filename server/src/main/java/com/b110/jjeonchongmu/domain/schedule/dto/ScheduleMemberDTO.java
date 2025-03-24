package com.b110.jjeonchongmu.domain.schedule.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleMemberDTO {
    private Long scheduleId;
    private Long scheduleMemberId;
    private String scheduleMemberName;
}