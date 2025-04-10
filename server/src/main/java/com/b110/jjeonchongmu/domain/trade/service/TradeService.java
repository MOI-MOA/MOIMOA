package com.b110.jjeonchongmu.domain.trade.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.trade.dto.TradeDetailDTO;
import com.b110.jjeonchongmu.domain.trade.dto.TradeHistoryRequestDTO;
import com.b110.jjeonchongmu.domain.trade.dto.TradeRequestDTO;
import com.b110.jjeonchongmu.domain.trade.dto.TradeResponseDTO;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 거래 관련 비즈니스 로직
 * 
 * [구현 기능]
 * 1. 거래 내역 조회
 *    - 계좌 존재 여부 확인
 *    - 계좌 비밀번호 검증
 *    - 계좌 유형별 거래 내역 조회
 *      - 입금 거래: toAccountId가 조회 계좌인 경우
 *      - 출금 거래: fromAccountId가 조회 계좌인 경우
 *    - 거래 내역 DTO 변환
 *      - 거래 유형 결정 (입금/출금)
 *      - 거래 정보 매핑
 * 
 * 2. 예외 처리
 *    - 계좌 없음
 *    - 비밀번호 불일치
 *    - 잘못된 계좌 유형
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class  TradeService {

    private final TradeRepo tradeRepo;
    private final UserRepo userRepo;
    private final GatheringRepo gatheringRepo;
    private final EntityManager em;

    public TradeResponseDTO getTradeHistory(Long userId, TradeHistoryRequestDTO tradeHistoryRequestDTO) {
        User user = userRepo.getUserByUserId(userId);
        List<Trade> trades = new ArrayList<>();
        String name = "";
        String accountNo = "";
        Long accountBalance = 0L;
        Long totalDeposit = 0L;
        Long totalWithdrawal = 0L;
        Long targetAccountId = 0L;
        switch (tradeHistoryRequestDTO.getAccountType()) {
            case GATHERING:
                Gathering gathering = gatheringRepo.getGatheringByGatheringId(tradeHistoryRequestDTO.getGatheringId());
                GatheringAccount gatheringAccount = gathering.getGatheringAccount();
                targetAccountId = gatheringAccount.getAccountId();
                trades = tradeRepo.getGatheringTradesByAccountIdOrderByTradeTimeDesc(
                        targetAccountId,
                        PageRequest.of(0, tradeHistoryRequestDTO.getLimit()));
                System.out.println("trades.size() = " + trades.size());
                name = gathering.getGatheringName();
                accountNo = gatheringAccount.getAccountNo();
                accountBalance = gatheringAccount.getAccountBalance();
                break;
            case PERSONAL:
                PersonalAccount personalAccount = user.getPersonalAccount();
                targetAccountId = personalAccount.getAccountId();
                trades = tradeRepo.getPersonalTradesByAccountIdOrderByTradeTimeDesc(
                        targetAccountId,
                        PageRequest.of(0, tradeHistoryRequestDTO.getLimit()));
                System.out.println("trades.size() = " + trades.size());
                name = user.getName();
                accountNo = personalAccount.getAccountNo();
                accountBalance = personalAccount.getAccountBalance();
                break;
        }

        List<TradeDetailDTO> tradeDetailDTOs = new ArrayList<>();
        // totalDeposit(입금)이랑 totalWithdrawal(송금) 계산하는 로직
        for (Trade trade : trades) {
            String tradeName;
            TradeDetailDTO tradeDetailDTO;
            Long tradeAmount = trade.getTradeAmount();
            if (Objects.equals(trade.getFromAccount().getAccountId(), targetAccountId)) {
                if (Hibernate.unproxy(trade.getToAccount()) instanceof GatheringAccount ga) {
                    tradeName = ga.getGathering().getGatheringName() + " 모임계좌";
                } else {
                    tradeName = trade.getFromAccount().getUser().getName();
                };
                // tradeAmount가 송금 이면 totalWithdrawal에서 뺴줘야 값 증가함.
                totalWithdrawal += tradeAmount;
                tradeAmount *= -1;
                tradeDetailDTO = TradeDetailDTO.builder()
                        .tradeName(tradeName)
                        .tradeDetail(trade.getTradeDetail()) //얘가 받는통장표시
                        .tradeTime(trade.getTradeTime())
                        .tradeAmount(tradeAmount)
                        .tradeBalance(trade.getTradeBalance())
                        .build();
            } else {
                tradeName = trade.getFromAccount().getUser().getName();
                totalDeposit += tradeAmount;
                tradeDetailDTO = TradeDetailDTO.builder()
                        .tradeName(tradeName)
                        .tradeDetail(trade.getTradeDetail()) //얘가 받는통장표시
                        .tradeTime(trade.getTradeTime())
                        .tradeAmount(tradeAmount)
                        .tradeBalance(trade.getToTradeBalance())
                        .build();
            }
            tradeDetailDTOs.add(tradeDetailDTO);
        }

        return TradeResponseDTO.builder()
                .name(name)
                .accountNo(accountNo)
                .accountBalance(accountBalance)
                .totalDeposit(totalDeposit)
                .totalWithdrawal(totalWithdrawal)
                .tradeList(tradeDetailDTOs)
                .build();
    }
}