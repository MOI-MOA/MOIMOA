package com.b110.jjeonchongmu.domain.trade.api;

import com.b110.jjeonchongmu.domain.trade.dto.TradeDetailDTO;
import com.b110.jjeonchongmu.domain.trade.dto.TradeHistoryRequestDTO;
import com.b110.jjeonchongmu.domain.trade.dto.TradeResponseDTO;
import com.b110.jjeonchongmu.domain.trade.service.TradeService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 거래 내역 조회 API 컨트롤러
 * <p>
 * [구현 기능] 1. 계좌 거래내역 조회 - GET /api/v1/trade - 요청 파라미터 검증 - accountType: 계좌 유형 (필수) - accountId: 계좌
 * ID (필수) - accountPw: 계좌 비밀번호 (필수)
 * <p>
 * - 응답 - 성공: 200 OK, 거래 내역 목록 - 실패: - 404: 계좌 없음 - 400: 잘못된 요청 (파라미터 오류) - 401: 계좌 비밀번호 불일치 - 500:
 * 서버 오류
 */
@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
@Builder
public class TradeController {

	private final TradeService tradeService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/account-history")
	public ResponseEntity<TradeResponseDTO> getTradeHistory(
			@RequestBody TradeHistoryRequestDTO request) {
//		Long userId = jwtTokenProvider.getUserId();
		Long userId = 1L;
		TradeResponseDTO response = tradeService.getTradeHistory(userId, request);
		return ResponseEntity.ok(response);
	}


	// 거래하기

	//
}
