package com.b110.jjeonchongmu.global.component;

import com.b110.jjeonchongmu.domain.account.dto.BankTransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.BankTransferResponseDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

	@Autowired
	public ExternalBankApiComponent(
			RestTemplateBuilder builder,
			@Value("${external.bank.api.url}") String apiUrl,
			@Value("${external.bank.api.key}") String apiKey) {
		this.restTemplate = builder.build();
		this.apiUrl = apiUrl;
		this.apiKey = apiKey;
	}

	@SneakyThrows
	public BankTransferResponseDTO externalTransfer(BankTransferRequestDTO request) {

		String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

		Map<String, Object> requestBody = new HashMap<>();
		Map<String, Object> header = new HashMap<>();

		header.put("apiName", "updateDemandDepositAccountTransfer");
		header.put("transmissionDate", currentDate);
		header.put("transmissionTime", currentTime);
		header.put("institutionCode", "00100");
		header.put("apiServiceCode", "createDemandDeposit");
		header.put("institutionTransactionUniqueNo", generate20DigitRandomNumber());
		header.put("apiKey", apiKey);
		header.put("userKey", request.getUserKey()); // 필요한 경우 값 설정

		requestBody.put("Header", header);

		requestBody.put("depositAccountNo", request.getToAccountNo());
		requestBody.put("depositTransactionSummary", "(수시입출금) : 입금(이체)");
		requestBody.put("transactionBalance", request.getAmount());
		requestBody.put("withdrawAccountNo", request.getFromAccountNo());
		requestBody.put("withdrawTransactionSummary", "(수시입출금) : 입금(이체)");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, httpHeaders);

		try {
			String transferUrl = "demandDeposit/updateDemandDepositAccountTransfer";
			ResponseEntity<BankTransferResponseDTO> response =
					restTemplate.exchange(apiUrl + transferUrl, HttpMethod.POST, entity, BankTransferResponseDTO.class);

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
}
