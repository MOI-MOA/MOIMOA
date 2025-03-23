package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringDetailResponseDTO {
    private GatheringDTO gathering;
    private List<GatheringMemberDTO> members;
}
