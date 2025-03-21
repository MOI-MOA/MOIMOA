package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringMemberDTO {

    private Long GatheringMemberId;
    private Long GatheringId;
    private String  GatheringMemberUserId;
    private Integer GatheringAttendCount;
    private Integer GatheringMemberAccountBalance;
    private Integer GatheringMemberAccountDeposit;
    private Boolean Gathering_payment_status; //납부상태
}