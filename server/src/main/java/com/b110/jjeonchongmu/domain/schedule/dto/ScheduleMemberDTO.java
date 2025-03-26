package com.b110.jjeonchongmu.domain.schedule.dto;

import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleMemberDTO {
    private Long scheduleId;
    private Long scheduleMemberId;
    private String scheduleMemberName;

    public static ScheduleMemberDTO from(ScheduleMember scheduleMember){
        return ScheduleMemberDTO.builder()
                .scheduleId(scheduleMember.getSchedule().getId())
                .scheduleMemberId(scheduleMember.getId())
                .scheduleMemberName(scheduleMember.getScheduleMember().getName())
                .build();
    }
}
