package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringMemberDTO {

    private Long gatheringMemberId;
    private Long gatheringId;
    private Long gatheringMemberUserId;
    private Integer gatheringAttendCount;
    private Integer gatheringMemberAccountBalance;
    private Integer gatheringMemberAccountDeposit;
    private Boolean gatheringPaymentStatus; //납부상태
}