package com.b110.jjeonchongmu.domain.group.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberDTO {
    private Long groupMemberId;
    private Long groupId;
    private String userId;
    private Integer groupAttendCount;
    private Integer groupMemberAccountBalance;
}