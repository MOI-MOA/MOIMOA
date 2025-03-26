package com.b110.jjeonchongmu.domain.mypage.dto.myaccount;

import java.time.LocalDateTime;

import com.b110.jjeonchongmu.domain.trade.dto.TradeHistoryDTO;
import com.b110.jjeonchongmu.domain.trade.enums.TradeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeDto {
    private String tradeDetail;
    private LocalDateTime tradeTime;
    private Long tradeAmount;
    private Long tradeBalance;
    private String tradePartnerName;

    public TradeDto(TradeHistoryDTO tradeHistoryDTO) {
        tradeDetail = tradeHistoryDTO.getTradeDetail();
        tradeTime = tradeHistoryDTO.getTradeDateTime();
        tradeBalance = tradeHistoryDTO.getTradeBalance();
        tradePartnerName = tradeHistoryDTO.getToUserName();

//        if (tradeHistoryDTO.getTradeType() == TradeType.PAYMENT) {
//            tradeAmount = tradeHistoryDTO.getTradeAmount() * -1;
//        }
    }
}