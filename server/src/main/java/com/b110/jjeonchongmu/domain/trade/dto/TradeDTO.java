package com.b110.jjeonchongmu.domain.trade.dto;
import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeDTO {
    private Long tradeId;
    private AccountType fromAccountType;
    private Long fromAccountId;
    private AccountType toAccountType;
    private Long toAccountId;
    private Long tradeAmount;
    private LocalDateTime tradeTime;
    private String tradeDetail;
    private Long tradeBalance;
}
