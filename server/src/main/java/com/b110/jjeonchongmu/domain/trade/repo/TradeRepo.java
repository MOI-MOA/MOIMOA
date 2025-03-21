package com.b110.jjeonchongmu.domain.trade.repo;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.trade.dto.TradeResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepo extends JpaRepository<TradeResponseDTO, Long> {

    // 특정 계좌(AccountType, AccountId)의 거래 내역 조회
    List<TradeResponseDTO> findByAccount_AccountTypeAndAccount_AccountId(AccountType accountType, Long accountId);
}