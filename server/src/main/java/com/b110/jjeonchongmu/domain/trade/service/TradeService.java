package com.b110.jjeonchongmu.domain.trade.service;

import com.b110.jjeonchongmu.domain.trade.dto.TradeRequestDTO;
import com.b110.jjeonchongmu.domain.trade.dto.TradeResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

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
public class TradeService {
    public TradeResponseDTO getTradeHistory(TradeRequestDTO request) {
            // 거래내역 조회 기능 구현
        return new TradeResponseDTO();
    }
}
