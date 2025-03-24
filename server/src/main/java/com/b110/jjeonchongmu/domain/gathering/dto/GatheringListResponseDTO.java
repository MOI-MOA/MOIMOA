package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringListResponseDTO {
    private List<GatheringDTO> gatherings;
}