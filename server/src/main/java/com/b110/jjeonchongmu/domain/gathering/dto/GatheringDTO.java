package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.Query;

@Getter
@NoArgsConstructor
public class GatheringDTO {
    private Long gatheringId;
    private Long managerId;
    private Long gatheringAccountId;
    private String gatheringName; //
    private String gatheringIntroduction; //
    private int memberCount; // 기본값 0에서 자동으로 추가.
    private int penaltyRate;
    private String depositDate;
    private Long basicFee;
    private Long gatheringDeposit;
    private Boolean isCheck;

    @Builder
    public GatheringDTO(Long gatheringId, Long managerId, Long gatheringAccountId, String gatheringName,
                       String gatheringIntroduction, int penaltyRate, String depositDate,
                       Long basicFee, Long gatheringDeposit, int memberCount) {
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

    public GatheringDTO(Long gatheringId, Long managerId, Long accountId,
                       String gatheringName, String gatheringIntroduction,
                       String depositDate, Long basicFee, Integer penaltyRate,
                       Long gatheringDeposit) {
        this.gatheringId = gatheringId;
        this.managerId = managerId;
        this.gatheringAccountId = accountId;
        this.gatheringName = gatheringName;
        this.gatheringIntroduction = gatheringIntroduction;
        this.depositDate = depositDate;
        this.basicFee = basicFee;
        this.penaltyRate = penaltyRate;
        this.memberCount = memberCount;
        this.gatheringDeposit = gatheringDeposit;
    }
}