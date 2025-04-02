package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeDetailDTO {
    private String tradeDetail;
    private LocalDateTime tradeTime;
    private Long tradeAmount;
    private Long tradeBalance;
}
