package com.b110.jjeonchongmu.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordCheckRequestDTO {
    private AccountType accountType;
    private Long accountId;
    private int accountPW;
}