package com.b110.jjeonchongmu.domain.schedule.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleMemberDTO {
    private Long scheduleMemberId;
    private Long scheduleId;
    private Long userId;
}