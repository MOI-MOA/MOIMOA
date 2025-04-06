package com.b110.jjeonchongmu.global.component;

import com.b110.jjeonchongmu.domain.account.dto.BankAccountResponseDTO;
import com.b110.jjeonchongmu.domain.account.dto.BankTransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.BankTransferResponseDTO;
import com.b110.jjeonchongmu.domain.account.dto.MakeExternalAccountDTO;
import com.b110.jjeonchongmu.domain.user.dto.response.MakeUserResponseDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalBankApiComponent {

	private final RestTemplate restTemplate;
	private final String apiUrl;
	private final String apiKey;
	private final String userUrl;

	private static final Logger log = LoggerFactory.getLogger(ExternalBankApiComponent.class);

	@Autowired
	public ExternalBankApiComponent(
			RestTemplateBuilder builder,
			@Value("${external.bank.api.url}") String apiUrl,
			@Value("${external.bank.api.key}") String apiKey,
			@Value("${external.user.api.url}") String userUrl) {
		this.restTemplate = builder.build();
		this.apiUrl = apiUrl;
		this.apiKey = apiKey;
		this.userUrl = userUrl;
	}

	public void sendTransferWithRetry(BankTransferRequestDTO requestDTO)
			throws IllegalAccessException {
		int maxRetries = 3;
		int retryDelayMs = 1000; // 1초

		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			try {
				BankTransferResponseDTO response = externalTransfer(requestDTO);
				log.info("외부 은행 API 호출 성공");
				return;
			} catch (Exception e) {
				log.error("외부 은행 API 호출 실패 (시도 {}/{}): {}", attempt, maxRetries, e.getMessage());

				if (attempt == maxRetries) {
					log.error("최대 재시도 횟수 초과. 송금 실패");
					throw new IllegalAccessException("외부 은행 API 호출 중 오류 발생");
				}
				try {
					log.info("{}ms 후 재시도 예정", retryDelayMs);
					Thread.sleep(retryDelayMs);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new IllegalAccessException("재시도 대기 중 인터럽트 발생");
				}
			}
		}
		// 코드가 여기까지 오는 경우는 없지만, 컴파일을 위해 필요
		throw new RuntimeException("예상치 못한 오류");
	}

	@SneakyThrows
	public BankTransferResponseDTO externalTransfer(BankTransferRequestDTO requestDTO) {
		// ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
		// String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		// String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
		ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
		String currentDate = LocalDate.now(koreaZoneId).format(DateTimeFormatter.ofPattern	("yyyyMMdd"));
		String currentTime = LocalTime.now(koreaZoneId).format(DateTimeFormatter.ofPattern	("HHmmss"));

		Map<String, Object> requestBody = createHeaderAndBody("updateDemandDepositAccountTransfer",
				"updateDemandDepositAccountTransfer", requestDTO.getUserKey());
		System.out.println(requestDTO.getUserKey() + "회원키");
		System.out.println(requestDTO.getToAccountNo() + "받는사람");
		System.out.println(requestDTO.getFromAccountNo() + "주는사람");
		requestBody.put("depositAccountNo", requestDTO.getToAccountNo());
		requestBody.put("depositTransactionSummary", "(수시입출금) : 입금(이체)");
		requestBody.put("transactionBalance", requestDTO.getAmount());
		requestBody.put("withdrawalAccountNo", requestDTO.getFromAccountNo());
		requestBody.put("withdrawalTransactionSummary", "(수시입출금) : 출금(이체)");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, httpHeaders);

		try {
			String transferUrl = "demandDeposit/updateDemandDepositAccountTransfer";
			ResponseEntity<BankTransferResponseDTO> response =
					restTemplate.exchange(apiUrl + transferUrl, HttpMethod.POST, entity,
							BankTransferResponseDTO.class);

			return response.getBody();
		} catch (RestClientException e) {
			// API 호출 예외 처리
			throw new IllegalAccessException("외부 api 요청에서 오류 발생");
		}
	}

	public static String generate20DigitRandomNumber() {
		StringBuilder sb = new StringBuilder(20);
		Random random = new Random();
		// 1~9 로 시작하는 20자리 난수 [1-9]xx...
		sb.append(random.nextInt(9) + 1);
		for (int i = 0; i < 19; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	public BankAccountResponseDTO externalMakeAccount(MakeExternalAccountDTO makeExternalAccountDTO) {
		log.info("외부 은행 API - 계좌 생성 요청 시작 - userKey: {}, accountType: {}", 
			makeExternalAccountDTO.getUserKey(), makeExternalAccountDTO.getExternalAccountType());
		
		Map<String, Object> requestBody = createHeaderAndBody("createDemandDepositAccount",
				"createDemandDepositAccount", makeExternalAccountDTO.getUserKey());
		requestBody.put("accountTypeUniqueNo", makeExternalAccountDTO.getExternalAccountType());
		
		log.info("외부 은행 API - 요청 본문: {}", requestBody);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, httpHeaders);
		
		try {
			String demandDepositUrl = "demandDeposit/createDemandDepositAccount";
			log.info("외부 은행 API 호출 - URL: {}", apiUrl + demandDepositUrl);
			
			ResponseEntity<BankAccountResponseDTO> response =
					restTemplate.exchange(apiUrl + demandDepositUrl, HttpMethod.POST, entity,
							BankAccountResponseDTO.class);
			
			log.info("외부 은행 API - 계좌 생성 성공 - accountNo: {}", response.getBody().getRec().getAccountNo());
			return response.getBody();
		} catch (Exception e) {
			log.error("외부 은행 API - 계좌 생성 실패 - userKey: {}, error: {}, stackTrace: {}", 
				makeExternalAccountDTO.getUserKey(), e.getMessage(), e.getStackTrace());
			throw new RuntimeException("외부 은행 계좌 생성 중 오류 발생", e);
		}
	}

	public MakeUserResponseDTO createBankAppUser(String email) {
		log.info("외부 은행 API - 회원 생성 요청 시작 - email: {}", email);
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("apiKey", apiKey);
		requestBody.put("userId", email);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, httpHeaders);
		try {
			String makeUserUrl = "member/";
			log.info("외부 은행 API 호출 - URL: {}", userUrl + makeUserUrl);
			ResponseEntity<MakeUserResponseDTO> response =
					restTemplate.exchange(userUrl + makeUserUrl, HttpMethod.POST, entity,
							MakeUserResponseDTO.class);
			log.info("외부 은행 API - 회원 생성 성공 - userKey: {}", response.getBody().getUserKey());
			return response.getBody();
		} catch (Exception e) {
			log.error("외부 은행 API - 회원 생성 실패 - email: {}, error: {}", email, e.getMessage());
			throw new RuntimeException("회원 생성 중 오류 발생", e);
		}
	}

	public Map<String, Object> createHeaderAndBody(String apiName, String apiServiceCode,
			String userKey) {
		Map<String, Object> requestBody = new HashMap<>();
		Map<String, Object> header = new HashMap<>();
		ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
		String currentDate = LocalDate.now(koreaZoneId).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String currentTime = LocalTime.now(koreaZoneId).format(DateTimeFormatter.ofPattern("HHmmss"));
		header.put("apiName", apiName);
		header.put("transmissionDate", currentDate);
		header.put("transmissionTime", currentTime);
		header.put("institutionCode", "00100");
		header.put("fintechAppNo", "001");
		header.put("apiServiceCode", apiServiceCode);
		header.put("institutionTransactionUniqueNo", generate20DigitRandomNumber());
		header.put("apiKey", apiKey);
		header.put("userKey", userKey);

		requestBody.put("Header", header);
		return requestBody;
	}

	/**
	 * 계좌조회 ssafy api
	 * user 객체와 accountNo를 넣으면
	 * BankAccountInquiryResponseDTO 객체 반환
	 */
//	public BankAccountInquiryResponseDTO inquireAccount(User user, String accountNo) {
//		String apiName = "inquireDemandDepositAccount";
//		String apiServiceCode = "inquireDemandDepositAccount";
//
//		// requestBody 생성
//		Map<String, Object> requestBody = createHeaderAndBody(
//				apiName, apiServiceCode, user.getUserKey());
//		requestBody.put("accountNo", accountNo);
//
//		// 통신용 데이터로 변환
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, httpHeaders);
//
//		// url에 데이터 보냄
//		try {
//			String transferUrl = "demandDeposit/inquireDemandDepositAccount";
//			ResponseEntity<BankAccountInquiryResponseDTO> response =
//					restTemplate.exchange(apiUrl + transferUrl, HttpMethod.POST, entity,
//							BankAccountInquiryResponseDTO.class);
//			return response.getBody();
//		} catch (Exception e) {
//			throw new RuntimeException("외부 은행 계좌 조회 중 오류 발생", e);
//		}
//	}

}


