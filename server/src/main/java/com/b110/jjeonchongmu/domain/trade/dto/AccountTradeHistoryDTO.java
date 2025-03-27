package com.b110.jjeonchongmu.domain.trade.dto;

import com.b110.jjeonchongmu.domain.account.dto.AccountDTO;
import com.b110.jjeonchongmu.domain.account.entity.Account;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTradeHistoryDTO {
  private AccountDTO accountDTO;
  private Long totalDeposit;
  private Long totalWithdrawal;
  private List<TradeHistoryDTO> tradeHistoryDTOList;
}
