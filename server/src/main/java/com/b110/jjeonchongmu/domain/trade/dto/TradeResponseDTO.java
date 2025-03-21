package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.account.dto.AccountDTO;
import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.entity.Trade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeResponseDTO {
    private Long accountNo;
    private Long accountBalance;
    private List<TradeDetailDTO> tradeList;

    // 엔티티 데이터를 DTO로 변환하는 정적 메서드
    public static TradeResponseDTO fromEntity(AccountDTO account, List<Trade> trades) {
        return new TradeResponseDTO(
                account.getAccountNo(),
                account.getAccountBalance(),
                trades.stream().map(TradeDetailDTO::new).collect(Collectors.toList())
        );
    }
}