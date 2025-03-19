package com.b110.jjeonchongmu.domain.account.api;

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
 */
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    // TODO: 구현 예정
}
