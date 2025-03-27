package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.enums.TradeType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TradeHistoryDTO {

	private final Long tradeId;
	private final Long tradeAmount; // 무조건 양수
	private final Long tradeBalance;
	private final String tradeDetail;
	private final LocalDateTime tradeDateTime;
	private final String toUserName;
	private final TradeType tradeType;

	public static TradeHistoryDTO from(Trade trade, Long accountId) {

		TradeType tradeType;
		String otherUserName;

		if (accountId.equals(trade.getFromAccount().getAccountId())) {
			tradeType = TradeType.WITHDRAWAL;
			otherUserName = trade.getToAccount().getUser().getName();
		}else {
			tradeType = TradeType.PAYMENT;
			otherUserName = trade.getFromAccount().getUser().getName();
		}
		TradeHistoryDTO tradeHistoryDTO = new TradeHistoryDTO(
				trade.getTradeId(),
				trade.getTradeAmount(),
				trade.getTradeBalance(),
				trade.getTradeDetail(),
				trade.getTradeTime(),
				otherUserName,
				tradeType
		);

		return tradeHistoryDTO;
	}
}
