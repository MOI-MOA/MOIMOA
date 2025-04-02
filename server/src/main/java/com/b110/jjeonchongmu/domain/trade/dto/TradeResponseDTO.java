package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.account.dto.AccountDTO;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeResponseDTO {
    private String name;
    private String accountNo;
    private Long accountBalance;
    private Long totalDeposit;
    private Long totalWithdrawal;
    private List<TradeDetailDTO> tradeList;

}