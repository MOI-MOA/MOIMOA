package com.b110.jjeonchongmu.domain.trade.api;

import com.b110.jjeonchongmu.domain.trade.dto.request.TradeHistoryRequest;
import com.b110.jjeonchongmu.domain.trade.dto.response.TradeHistoryResponse;
import com.b110.jjeonchongmu.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 거래 내역 조회 API 컨트롤러
 * 
 * [구현 기능]
 * 1. 계좌 거래내역 조회 - GET /api/v1/trade
 *    - 요청 파라미터 검증
 *      - accountType: 계좌 유형 (필수)
 *      - accountId: 계좌 ID (필수)
 *      - accountPw: 계좌 비밀번호 (필수)
 * 
 *    - 응답
 *      - 성공: 200 OK, 거래 내역 목록
 *      - 실패: 
 *        - 404: 계좌 없음
 *        - 400: 잘못된 요청 (파라미터 오류)
 *        - 401: 계좌 비밀번호 불일치
 *        - 500: 서버 오류
 */
@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;

    @GetMapping
    public ResponseEntity<?> getTradeHistory(
            @RequestParam String accountType,
            @RequestParam Integer accountId,
            @RequestParam Integer accountPw) {
        
        TradeHistoryRequest request = TradeHistoryRequest.builder()
                .accountType(accountType)
                .accountId(accountId)
                .accountPw(accountPw)
                .build();

        TradeHistoryResponse response = tradeService.getTradeHistory(request);
        return ResponseEntity.ok(new ApiResponse(200, "거래 내역 조회 성공", response));
    }
}
