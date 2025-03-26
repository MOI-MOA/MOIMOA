package com.b110.jjeonchongmu.domain.account.api;

import com.b110.jjeonchongmu.domain.account.dto.*;
import com.b110.jjeonchongmu.domain.account.service.GatheringAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

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
@RequestMapping("/api/v1/Gathering-account")
@RequiredArgsConstructor
public class GatheringAccountController {
    private final GatheringAccountService gatheringAccountService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    /**
     * 계좌 송금
     */
    @PostMapping("/transfer")

    public ResponseEntity<Object> transfer(
            @RequestBody TransferRequestDTO requestDto) {
        TransferTransactionHistoryDTO response = gatheringAccountService.initTransfer(requestDto);

        CompletableFuture.runAsync(() -> {
            try {
                // 성공하면 계좌 잔액을 함께 보내기??
                boolean isCompleted = gatheringAccountService.processTransfer(response);

                simpMessagingTemplate.convertAndSend(
                        "/queue/transfer-results" + requestDto.getToAccountId(),
                        isCompleted
                );
            } catch (Exception e) {

                TransferResponseDTO result = new TransferResponseDTO();
                simpMessagingTemplate.convertAndSend(
                        "/queue/transfer-results" + requestDto.getToAccountId(),
                        "송금중 오류가 발생"
                );
            }
        });
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * 계좌 비밀번호 확인
     */
    @PostMapping("/password/check")
    public ResponseEntity<Boolean> checkPassword(@RequestBody PasswordCheckRequestDTO requestDto) {
        Boolean response = gatheringAccountService.checkPassword(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 계좌 삭제 (일정 계좌 삭제 포함)
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteRequestDTO requestDTO) {
        gatheringAccountService.deleteAccount(requestDTO);
        return ResponseEntity.ok("계좌 삭제 성공");
    }
}
