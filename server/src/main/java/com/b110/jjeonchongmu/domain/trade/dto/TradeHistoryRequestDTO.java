package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TradeHistoryRequestDTO {
	private AccountType accountType;
	private Long gatheringId;
	private Integer limit;
}
