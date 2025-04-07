package com.b110.jjeonchongmu.domain.account.api;

import com.b110.jjeonchongmu.domain.account.dto.AutoPaymentDTO;
import com.b110.jjeonchongmu.domain.account.service.AutoPaymentService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 자동이체 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/auto-payment")
@RequiredArgsConstructor
public class AutoPaymentController {

    private final AutoPaymentService autoPaymentService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자의 모든 자동이체 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<AutoPaymentDTO>> getAllAutoPayments() {
        Long userId = jwtTokenProvider.getUserId();
        List<AutoPaymentDTO> autoPayments = autoPaymentService.getAllAutoPaymentsByUserId(userId);
        return ResponseEntity.ok(autoPayments);
    }

    /**
     * 자동이체 활성화/비활성화
     */
    @PutMapping("/{autoPaymentId}/status")
    public ResponseEntity<Void> updateAutoPaymentStatus(
            @PathVariable Long autoPaymentId,
            @RequestParam Boolean isActive) {
        autoPaymentService.updateAutoPaymentStatus(autoPaymentId, isActive);
        return ResponseEntity.ok().build();
    }

    /**
     * 자동이체 수정 (금액, 날짜)
     */
    @PutMapping("/{autoPaymentId}")
    public ResponseEntity<Void> updateAutoPayment(
            @PathVariable Long autoPaymentId,
            @RequestParam(required = false) Long amount,
            @RequestParam(required = false) Integer date) {
        autoPaymentService.updateAutoPayment(autoPaymentId, amount, date);
        return ResponseEntity.ok().build();
    }

    /**
     * 자동이체 삭제
     */
    @DeleteMapping("/{autoPaymentId}")
    public ResponseEntity<Void> deleteAutoPayment(@PathVariable Long autoPaymentId) {
        autoPaymentService.deleteAutoPayment(autoPaymentId);
        return ResponseEntity.ok().build();
    }
} 