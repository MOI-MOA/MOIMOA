package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GatheringDTO {
    private Long gatheringId;
    private Long managerId;
    private Long gatheringAccountId;
    private String gatheringName;
    private String gatheringIntroduction;
    private int memberCount;
    private int penaltyRate;
    private String depositDate;
    private int basicFee;
    private int gatheringDeposit;

    @Builder
    public GatheringDTO(Long gatheringId, Long managerId, Long gatheringAccountId, String gatheringName,
                       String gatheringIntroduction, int memberCount, int penaltyRate, String depositDate,
                       int basicFee, int gatheringDeposit) {
        this.gatheringId = gatheringId;
        this.managerId = managerId;
        this.gatheringAccountId = gatheringAccountId;
        this.gatheringName = gatheringName;
        this.gatheringIntroduction = gatheringIntroduction;
        this.memberCount = memberCount;
        this.penaltyRate = penaltyRate;
        this.depositDate = depositDate;
        this.basicFee = basicFee;
        this.gatheringDeposit = gatheringDeposit;
    }
}