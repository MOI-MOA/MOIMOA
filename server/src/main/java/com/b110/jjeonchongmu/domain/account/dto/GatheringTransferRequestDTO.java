package com.b110.jjeonchongmu.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatheringTransferRequestDTO {
    private Long groupId;
    private Long scheduleId;
    private String accountNo;
    private Integer amount;
    private String accountPw;
    private String tradeDetail;
}
