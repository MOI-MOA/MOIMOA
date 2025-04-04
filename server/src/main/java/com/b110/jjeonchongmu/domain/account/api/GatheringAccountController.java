package com.b110.jjeonchongmu.domain.account.api;

import com.b110.jjeonchongmu.domain.account.dto.DeleteRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.GatheringTransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.PasswordCheckRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferResponseDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferTransactionHistoryDTO;
import com.b110.jjeonchongmu.domain.account.repo.AccountRepo;
import com.b110.jjeonchongmu.domain.account.service.GatheringAccountService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/v1/gathering-account")
@RequiredArgsConstructor
public class GatheringAccountController {
    private final GatheringAccountService gatheringAccountService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepo accountRepo;

    /**
     * 계좌 송금
     */
    @PostMapping("/transfer")

    public ResponseEntity<Object> transfer(
            @RequestBody GatheringTransferRequestDTO transferRequestDto) {

        Long userId = jwtTokenProvider.getUserId();
        TransferRequestDTO requestDto = gatheringAccountService.getTransferRequestDto(userId, transferRequestDto);
        TransferTransactionHistoryDTO response = gatheringAccountService.initTransfer(requestDto);

        CompletableFuture.runAsync(() -> {
            try {
                // 성공하면 계좌 잔액을 함께 보내기??
                boolean isCompleted = gatheringAccountService.processTransfer(response);

                simpMessagingTemplate.convertAndSend(
                        "/queue/transfer-results" + userId,
                        isCompleted
                );
            } catch (Exception e) {

                TransferResponseDTO result = new TransferResponseDTO();
                simpMessagingTemplate.convertAndSend(
                        "/queue/transfer-results" + userId,
                        "송금중 오류가 발생"
                );
            }
        });
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    /**
     * 계좌 존재하는지 확인
     * 존재하면 200 ok
     * 존재하지 않으면 404 notFound
     */
    @GetMapping("/account/{accountNo}/check")
    public ResponseEntity<Map<String, String>> checkAccount(@PathVariable String accountNo) {
        Map<String, String> map = new HashMap<>();
        Long userId = jwtTokenProvider.getUserId();
        String name;
        try {
            // 계좌 찾고
            name = gatheringAccountService.findNameByAccountNo(accountNo);
        } catch (Exception e) {
            // 없으면 실패 메시지 담아서 return
            e.printStackTrace();
            map.put("message", "account를 찾을때 문제가 생겼습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        // 있으면 name담아서 리턴
        map.put("name", name);

        return ResponseEntity.status(HttpStatus.OK).body(map);
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

