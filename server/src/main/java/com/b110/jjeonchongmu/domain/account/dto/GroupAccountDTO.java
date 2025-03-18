package com.b110.jjeonchongmu.domain.account.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAccountDTO {
    private Long groupAccountId;
    private Long groupId;
    private Long groupAccountNo;
    private Integer groupAccountBalance;
}