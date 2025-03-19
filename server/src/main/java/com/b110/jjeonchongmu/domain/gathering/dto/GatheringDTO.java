package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringDTO {
    private Long groupId;
    private String groupName;
    private String groupIntroduction;
    private Integer memberCount;
    private Integer groupDeposit;
}