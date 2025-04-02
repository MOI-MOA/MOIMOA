package com.b110.jjeonchongmu.domain.account.api;

import com.b110.jjeonchongmu.domain.account.dto.AccountCheckResponseDTO;
import com.b110.jjeonchongmu.domain.account.dto.AddAutoPaymentRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.DeleteRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.PasswordCheckRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferResponseDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferTransactionHistoryDTO;
import com.b110.jjeonchongmu.domain.account.service.PersonalAccountService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import java.util.List;
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
 * <p>
 * 1. 송금하기 - POST /api/v1/account/transfer - Request: fromAccountType, fromAccountId, toAccountType,
 * toAccountId, amount, tradeDetail
 * <p>
 * 2. 계좌 비밀번호 확인 - POST /api/v1/account/password/check - Request: accountType, accountId, accountPW
 * <p>
 * 3. 자동이체 현황 조회 - GET /api/v1/account/autopayment
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-account")
public class PersonalAccountController {

	private final PersonalAccountService personalAccountService;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 계좌 송금
	 */
	@PostMapping("/transfer")
	public ResponseEntity<Object> transfer(
			@RequestBody TransferRequestDTO requestDto) {
		Long userId = jwtTokenProvider.getUserId();
		TransferTransactionHistoryDTO response = personalAccountService.initTransfer(requestDto);

		CompletableFuture.runAsync(() -> {
			try {
				// 성공하면 계좌 잔액을 함께 보내기??
				boolean isCompleted = personalAccountService.processTransfer(response, userId);

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
	 * 계좌 비밀번호 확인
	 */
	@PostMapping("/password/check")
	public ResponseEntity<Boolean> checkPassword(@RequestBody PasswordCheckRequestDTO requestDto) {
		Boolean response = personalAccountService.checkPassword(requestDto);
		return ResponseEntity.ok(response);
	}

	// 송금할 계좌번호 확인
	@GetMapping("/{accountNo}/{amount}/check")
	public ResponseEntity<AccountCheckResponseDTO> checkAccountNo(
			@PathVariable String accountNo, @PathVariable Long amount) {
		AccountCheckResponseDTO response = personalAccountService.checkAccountNo(
				accountNo, amount);
		return ResponseEntity.ok(response);
	}

	/**
	 * 자동이체 추가
	 */
	@PostMapping("/autopayment")
	public ResponseEntity<String> addAutoPayment() {
		personalAccountService.addAutoPayment();
		return ResponseEntity.ok("자동이체 추가 성공");
	}

	/**
	 * 자동이체 현황 조회
	 */
	@GetMapping("/autopayment")
	public ResponseEntity<List<AddAutoPaymentRequestDTO>> getAutoPayments() {
		List<AddAutoPaymentRequestDTO> response = personalAccountService.getAutoPayments();
		return ResponseEntity.ok(response);
	}

	/**
	 * 계좌 삭제 (일정 계좌 삭제 포함)
	 */
	@DeleteMapping
	public ResponseEntity<String> deleteAccount(@RequestBody DeleteRequestDTO requestDTO) {
		personalAccountService.deleteAccount(requestDTO);
		return ResponseEntity.ok("계좌 삭제 성공");
	}

}

