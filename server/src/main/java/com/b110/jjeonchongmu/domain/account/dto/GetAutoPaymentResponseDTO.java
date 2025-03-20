package com.b110.jjeonchongmu.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetAutoPaymentResponseDTO {
    private Long autoPaymentNo;
    private int autoPaymentAmount;
    private Date autoPaymentDate;
    private boolean isActive;
    private String gatheringName;
    private long gatheringDeposit;
    private long gatheringAccountNumber;
    private long gatheringAccountBalance;
}