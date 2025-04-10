package com.b110.jjeonchongmu.domain.trade.repo;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.trade.dto.TradeResponseDTO;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepo extends JpaRepository<Trade, Long> {

	// 특정 계좌(AccountType, AccountId)의 거래 내역


	@Query("select t from Trade t where (t.toAccountType = :accountType and t.toAccount.accountId = :accountId) or (t.fromAccountType = :accountType and t.fromAccount.accountId = :accountId) order by t.tradeTime desc")
	List<Trade> findByAccountId(long accountId, AccountType accountType);


	@Query("""
    SELECT t
    FROM Trade t
    WHERE
        (
            (t.fromAccountType = com.b110.jjeonchongmu.domain.account.dto.AccountType.GATHERING
            AND t.fromAccount.accountId = :accountId)
            OR
            (t.toAccountType = com.b110.jjeonchongmu.domain.account.dto.AccountType.GATHERING
            AND t.toAccount.accountId = :accountId)
        )
    ORDER BY t.tradeTime DESC
""")
    List<Trade> getGatheringTradesByAccountIdOrderByTradeTimeDesc(
			Long accountId,
			Pageable pageable);


//	@Query("""
//    SELECT t
//    FROM Trade t
//    WHERE
//        (t.fromAccountType = com.b110.jjeonchongmu.domain.account.dto.AccountType.PERSONAL
//         OR t.toAccountType = com.b110.jjeonchongmu.domain.account.dto.AccountType.PERSONAL)
//      AND (t.fromAccount.accountId = :accountId OR t.toAccount.accountId = :accountId)
//    ORDER BY t.tradeTime DESC
//""")
@Query("""
    SELECT t
    FROM Trade t
    WHERE
        (
            (t.fromAccountType = com.b110.jjeonchongmu.domain.account.dto.AccountType.PERSONAL
            AND t.fromAccount.accountId = :accountId)
            OR
            (t.toAccountType = com.b110.jjeonchongmu.domain.account.dto.AccountType.PERSONAL
            AND t.toAccount.accountId = :accountId)
        )
    ORDER BY t.tradeTime DESC
""")
	List<Trade> getPersonalTradesByAccountIdOrderByTradeTimeDesc(
			Long accountId,
			Pageable pageable);
}