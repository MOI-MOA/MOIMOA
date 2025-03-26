package com.b110.jjeonchongmu.domain.mypage.dto.myaccount;

import com.b110.jjeonchongmu.domain.trade.dto.TradeHistoryDTO;
import com.b110.jjeonchongmu.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyAccountResponseDto {
    private String gatheringName;
    private String gatheringAccountNo;
    private Long gatheringAccountBalance;
    private List<TradeDto> tradeList = new ArrayList<>();

    public MyAccountResponseDto(User user, List<TradeHistoryDTO> tradeHistoryDTOList) {
        this.gatheringName = user.getName();
        this.gatheringAccountNo = user.getPersonalAccount().getAccountNo();
        this.gatheringAccountBalance = user.getPersonalAccount().getAccountBalance();
        for (TradeHistoryDTO tradeHistoryDTO : tradeHistoryDTOList) {
            tradeList.add(new TradeDto(tradeHistoryDTO));
        }
    }
}

