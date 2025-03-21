package com.b110.jjeonchongmu.domain.account.api;

import com.b110.jjeonchongmu.domain.account.dto.*;
import com.b110.jjeonchongmu.domain.account.service.ScheduleAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

/**
 * 계좌 관련 API 컨트롤러
 *
 * 1. 송금하기 - POST /api/v1/account/transfer
 *    - Request: fromAccountType, fromAccountId, toAccountType, toAccountId, amount, tradeDetail
 *
 * 2. 계좌 비밀번호 확인 - POST /api/v1/account/password/check
 *    - Request: accountType, accountId, accountPW
 *
 * 3. 자동이체 현황 조회 - GET /api/v1/account/autopayment
 *
 */
@RestController
@RequestMapping("/api/v1/schedule-account")
@RequiredArgsConstructor
public class ScehduleAccountController {
    private final ScheduleAccountService scheduleAccountService;
    /**
     * 계좌 송금
     */
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO requestDto) {
        String response = scheduleAccountService.transfer(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 계좌 비밀번호 확인
     */
    @PostMapping("/password/check")
    public ResponseEntity<Boolean> checkPassword(@RequestBody PasswordCheckRequestDTO requestDto) {
        Boolean response = scheduleAccountService.checkPassword(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 계좌 삭제 (일정 계좌 삭제 포함)
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteRequestDTO requestDTO) {
        scheduleAccountService.deleteAccount(requestDTO);
        return ResponseEntity.ok("계좌 삭제 성공");
    }
}
