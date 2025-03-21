package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GatheringDetailResponseDTO {
    private GatheringDTO gathering;
    private List<GatheringMemberDTO> members;
}
