package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.*;

import java.util.Date;

@Builder
@Data
public class GatheringDTO {
    private Long gatheringId;
    private String gatheringName;
    private String gatheringIntroduction;
    private String depositDate;
    private Long basicFee;
    private int penaltyRate;
    private Integer memberCount;
    private Long gatheringDeposit;

}