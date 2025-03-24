package com.b110.jjeonchongmu.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddAutoPaymentRequestDTO {
    private Long fromAccountId;
    private Long toAccountId;
    private Long autoPayMentAmount;
    private Date autoPaymentDate;
    private long gatheringId;
}