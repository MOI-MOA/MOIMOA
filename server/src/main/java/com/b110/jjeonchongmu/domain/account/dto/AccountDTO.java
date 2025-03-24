package com.b110.jjeonchongmu.domain.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private Long accountHolderId;
    private Long accountNo;
    private Long accountBalance;
    private int accountPw;
}