package com.b110.jjeonchongmu.domain.mypage.api;

import com.b110.jjeonchongmu.domain.mypage.dto.*;
import com.b110.jjeonchongmu.domain.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{type}-account/autopayment")
    public ResponseEntity<List<AutoPaymentResponse>> getAutoPayments(@PathVariable String type) {
        return ResponseEntity.ok(myPageService.getAutoPayments(type));
    }

    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponse> getMyPage() {
        return ResponseEntity.ok(myPageService.getMyPage());
    }

    @GetMapping("/mypage/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        return ResponseEntity.ok(myPageService.getStatistics());
    }
}
