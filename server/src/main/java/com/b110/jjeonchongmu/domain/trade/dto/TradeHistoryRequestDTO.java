package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import lombok.Getter;

@Getter
public class TradeHistoryRequestDTO {
	private AccountType accountType;
	private long accountId;
	private long accountPw;
}
