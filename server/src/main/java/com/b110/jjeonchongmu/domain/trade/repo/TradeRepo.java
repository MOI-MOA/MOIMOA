package com.b110.jjeonchongmu.domain.trade.repo;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.trade.dto.TradeResponseDTO;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepo extends JpaRepository<TradeResponseDTO, Long> {

	// 특정 계좌(AccountType, AccountId)의 거래 내역 조회
	List<TradeResponseDTO> findByAccount_AccountTypeAndAccount_AccountId(AccountType accountType,
			Long accountId);


	@Query("select t from Trade t where (t.toAccountType = :accountType and t.toAccount.accountId = :accountId) or (t.fromAccountType = :accountType and t.fromAccount.accountId = :accountId) order by t.tradeTime desc")
	List<Trade> findByAccountId(long accountId, AccountType accountType);

}