package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Builder
@Data
public class GatheringListResponseDTO {
    private Long gatheringId;
    private String gatheringName;
    private String gatheringIntroduction;
    private String depositDate;
    private Long basicFee;
    private int penaltyRate;
    private Integer memberCount;
    private Long gatheringDeposit;

    private List<GatheringDTO> gatherings;

}
