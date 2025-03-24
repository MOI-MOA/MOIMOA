package com.b110.jjeonchongmu.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoPaymentResponse {
    private Long autoPaymentNo;
    private int autoPaymentAmount;
    private LocalDateTime autoPaymentDate;
    private boolean isActive;
    private String gatheringName;
    private Long gatheringDeposit;
    private Long gatheringAccountNo;
    private Long gatheringAccountBalance;
} 