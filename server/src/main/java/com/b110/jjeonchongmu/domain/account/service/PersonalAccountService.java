package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.dto.AddAutoPaymentRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.DeleteRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.PasswordCheckRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferResponseDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferTransactionHistoryDTO;
import com.b110.jjeonchongmu.domain.account.enums.TransactionStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PersonalAccountService {

	public TransferTransactionHistoryDTO initTransfer(TransferRequestDTO requestDto) {

		// 초기 송금기록
		return TransferTransactionHistoryDTO.builder()
				.fromAccountId(requestDto.getFromAccountId())
				.fromAccountType(requestDto.getFromAccountType())
				.toAccountId(requestDto.getToAccountId())
				.toAccountType(requestDto.getToAccountType())
				.amount(requestDto.getTransferAmount())
				.detail(requestDto.getTradeDetail())
				.status(TransactionStatus.BEFORE)
				.createdAt(LocalDateTime.now())
				.build();
	}

	public TransferResponseDTO processTransfer(
			TransferTransactionHistoryDTO transferTransactionHistoryDTO) {
		return null;
	}

	public Boolean checkPassword(PasswordCheckRequestDTO requestDto) {
		// 비밀번호 확인 로직 구현
		return true;
	}

	public void addAutoPayment() {
		// 자동이체 추가 로직 구현
	}

	public List<AddAutoPaymentRequestDTO> getAutoPayments() {
		// 자동이체 조회 로직 구현
		return new ArrayList<>();
	}

	public void deleteAccount(DeleteRequestDTO requestDTO) {
		// 계좌 삭제 로직 구현
	}
}
