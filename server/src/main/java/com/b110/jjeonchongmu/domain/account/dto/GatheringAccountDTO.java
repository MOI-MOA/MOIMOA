package com.b110.jjeonchongmu.domain.account.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringAccountDTO {
    private Long groupAccountId;
    private AccountDTO accountDTO;
}