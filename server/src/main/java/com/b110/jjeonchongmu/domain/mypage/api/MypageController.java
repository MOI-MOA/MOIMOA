package com.b110.jjeonchongmu.domain.mypage.api;

import com.b110.jjeonchongmu.domain.mypage.dto.*;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.AutoPaymentResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.StatisticsResponse;
import com.b110.jjeonchongmu.domain.mypage.service.MypageService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 마이페이지 관련 API 컨트롤러
 *
 * 1. 마이페이지 조회 - GET /api/v1/mypage
 *    - Response: userId, name, email, scheduleNotificationEnabled
 *
 * 2. 통계 현황 조회 - GET /api/v1/mypage/statistics
 *    - Response:
 *      - 총 지출액
 *      - 월평균 지출액
 *      - 최다 지출 모임
 *      - 최다 지출 월
 *      - 평균 참석률
 *      - 최다 참석 모임
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MypageController {

    private final MypageService myPageService;
    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping("/autopayment")
    public ResponseEntity<AutoPaymentResponse> getAutoPayments() {
        Long id = jwtTokenProvider.getUserId();
        return ResponseEntity.ok(myPageService.getAutoPaymentResponseByUserId(id));
    }

    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponse> getMyPage() {
        Long id = jwtTokenProvider.getUserId();
        return ResponseEntity.ok(myPageService.getMyPage(id));
    }

    @GetMapping("/mypage/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        Long id = jwtTokenProvider.getUserId();
        return ResponseEntity.ok(myPageService.getStatistics(id));
    }
}
