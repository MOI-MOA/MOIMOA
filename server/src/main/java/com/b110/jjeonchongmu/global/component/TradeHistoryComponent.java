package com.b110.jjeonchongmu.global.component;

import com.b110.jjeonchongmu.domain.trade.dto.TradeHistoryDTO;
import com.b110.jjeonchongmu.domain.trade.dto.TradeHistoryRequestDTO;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeHistoryComponent {

	private TradeRepo tradeRepo;

	public List<TradeHistoryDTO> getTradeHistory(TradeHistoryRequestDTO tradeHistoryRequestDTO) {
		List<Trade> trades = tradeRepo.findByAccountId(tradeHistoryRequestDTO.getAccountId(),
				tradeHistoryRequestDTO.getAccountType());
		return trades.stream().map(trade -> TradeHistoryDTO.from(trade, tradeHistoryRequestDTO.getAccountId()))
				.collect(
						Collectors.toList());
	}


}
