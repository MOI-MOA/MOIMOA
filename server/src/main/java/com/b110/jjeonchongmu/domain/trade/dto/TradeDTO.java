package com.b110.jjeonchongmu.domain.trade.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeDTO {
    private Long tradeId;
    private String userId;
    private String tradeType;
    private String accountType;
    private Long fromAccountId;
    private Long toAccountId;
    private Integer tradeAmount;
    private LocalDateTime tradeTime;
    private String tradeDetail;
}