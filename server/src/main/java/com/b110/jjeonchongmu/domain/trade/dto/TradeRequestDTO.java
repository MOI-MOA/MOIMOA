package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequestDTO {
  private AccountType fromAccountType;
  private long fromAccountId;
  private long fromAccountPw;
  private AccountType toAccountType;
  private Long toAccountId;
  private Long toAccountPw;
}
