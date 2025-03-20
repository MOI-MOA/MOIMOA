package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.entity.Trade;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@AllArgsConstructor
public class TradeDetailDTO {
    private String tradeDetail;
    private Date tradeTime;
    private Long tradeAmount;
    private Long tradeBalance;
    private String tradePartnerName;

    public TradeDetailDTO(Trade trade) {
        String tradePartnerName = null;

        // if 안에 조건문 고쳐야함 ( token에서 내 정보 가져와서 내 계좌와 fromaccount 또는 toaccount 가 같으면 )
        if (true) {
            tradePartnerName = trade.getFromAccount().getAccountHolder().getName();
        } else if (true) {
            tradePartnerName = trade.getToAccount().getAccountHolder().getName();
        }

        this.tradeDetail = trade.getTradeDetail();
        this.tradeTime = trade.getTradeTime();
        this.tradeAmount = trade.getTradeAmount();
        this.tradeBalance = trade.getTradeBalance();
        this.tradePartnerName = tradePartnerName;
    }


    // Trade 엔티티를 DTO로 변환하는 정적 메서드
    public static TradeDetailDTO fromEntity(Trade trade) {
        String tradePartnerName = null;

        // if 안에 조건문 고쳐야함 ( token에서 내 정보 가져와서 내 계좌와 fromaccount 또는 toaccount 가 같으면 )
        if (true) {
            tradePartnerName = trade.getFromAccount().getAccountHolder().getName();
        } else if (true) {
            tradePartnerName = trade.getToAccount().getAccountHolder().getName();
        }

        return new TradeDetailDTO(
                trade.getTradeDetail(),
                trade.getTradeTime(),
                trade.getTradeAmount(),
                trade.getTradeBalance(),
                tradePartnerName
        );
    }
}
