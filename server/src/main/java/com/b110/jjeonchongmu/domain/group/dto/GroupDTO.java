package com.b110.jjeonchongmu.domain.group.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private Long groupId;
    private String groupName;
    private String groupIntroduction;
    private Integer memberCount;
    private Integer groupDeposit;
}