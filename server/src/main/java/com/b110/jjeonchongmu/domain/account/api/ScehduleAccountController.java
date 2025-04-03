package com.b110.jjeonchongmu.domain.account.api;

import com.b110.jjeonchongmu.domain.account.dto.*;
import com.b110.jjeonchongmu.domain.account.service.ScheduleAccountService;
import org.apache.coyote.Response;
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
        TransferTransactionHistoryDTO response = scheduleAccountService.initTransfer(requestDto);

        scheduleAccountService.processTransfer(response);
        return ResponseEntity.status(HttpStatus.CREATED).body("계좌 송금 성공");

    }
    /**
     * 계좌 비밀번호 확인
     */

    @PostMapping("/password/check")
    public ResponseEntity<Boolean> checkPassword(@RequestBody PasswordCheckRequestDTO requestDto) {
        return ResponseEntity.ok(scheduleAccountService.checkPassword(requestDto));
    }

    /**
     * 계좌 삭제
     */

    @DeleteMapping
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteRequestDTO requestDTO) {
        if(scheduleAccountService.deleteAccount(requestDTO)){
             return ResponseEntity.ok("계좌 삭제 성공");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게좌 삭제 실패");
    }


}
