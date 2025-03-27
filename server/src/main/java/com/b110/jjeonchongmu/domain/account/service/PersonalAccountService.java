package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.dto.AccountCheckResponseDTO;
import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.account.dto.AddAutoPaymentRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.BankAccountResponseDTO;
import com.b110.jjeonchongmu.domain.account.dto.BankTransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.DeleteRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.MakeExternalAccountDTO;
import com.b110.jjeonchongmu.domain.account.dto.PasswordCheckRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferTransactionHistoryDTO;
import com.b110.jjeonchongmu.domain.account.entity.Account;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.account.enums.TransactionStatus;
import com.b110.jjeonchongmu.domain.account.repo.AccountRepo;
import com.b110.jjeonchongmu.domain.account.repo.GatheringAccountRepo;
import com.b110.jjeonchongmu.domain.account.repo.PersonalAccountRepo;
import com.b110.jjeonchongmu.domain.account.repo.ScheduleAccountRepo;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.component.ExternalBankApiComponent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonalAccountService {

	private final PasswordEncoder passwordEncoder;
	private final ExternalBankApiComponent externalBankApiComponent;
	private final PersonalAccountRepo personalAccountRepo;
	private final GatheringAccountRepo gatheringAccountRepo;
	private final ScheduleAccountRepo scheduleAccountRepo;
	private final AccountRepo accountRepo;
	private final TradeRepo tradeRepo;
	private final UserRepo userRepo;

	@Transactional
	public TransferTransactionHistoryDTO initTransfer(TransferRequestDTO requestDto) {

		// 계좌를 db에서 확인
		System.out.println(requestDto.getToAccountNo());
		Account account = accountRepo.findAccountByAccountNo(requestDto.getToAccountNo());
		System.out.println(account);

		// 초기 송금기록
		return TransferTransactionHistoryDTO.builder()
				.fromAccountId(requestDto.getFromAccountId())
				.fromAccountType(requestDto.getFromAccountType())
				.toAccountId(account.getAccountId())
				.toAccountType(account.getDtype())
				.amount(requestDto.getTransferAmount())
				.detail(requestDto.getTradeDetail())
				.status(TransactionStatus.BEFORE)
				.createdAt(LocalDateTime.now())
				.accountPw(requestDto.getAccountPw())
				.build();
	}

	@Transactional
	public boolean processTransfer(
			TransferTransactionHistoryDTO transferTransactionHistoryDTO) {

		try {
			transferTransactionHistoryDTO.updateStatus(TransactionStatus.PROCESSING);
			PersonalAccount fromAccount = personalAccountRepo.findByAccount(
							transferTransactionHistoryDTO.getFromAccountId())
					.orElseThrow(() -> new IllegalArgumentException("입금 계좌를 가져오는중 오류발생"));

			String originAccountPw = fromAccount.getAccountPw();
			String userAccountPw = transferTransactionHistoryDTO.getAccountPw();
			System.out.println(originAccountPw + " , " + userAccountPw);
			boolean isPassword = passwordEncoder.matches(userAccountPw, originAccountPw);
			if(!isPassword) {
				throw new IllegalAccessException("비밀번호 불일치");
			}

			// 잔액 검증
			if (fromAccount.getAccountBalance() < transferTransactionHistoryDTO.getAmount()) {
				transferTransactionHistoryDTO.updateStatus(TransactionStatus.FAILED);
				throw new IllegalStateException("잔액이 부족합니다");
			}

			// 계좌 타입에 따라 입금 계좌 조회
			Account toAccount;
			String successMessage;
			toAccount = switch (transferTransactionHistoryDTO.getToAccountType()) {
				case GATHERING -> gatheringAccountRepo.findByAccount(
								transferTransactionHistoryDTO.getToAccountId())
						.orElseThrow(() -> new IllegalArgumentException("모임계좌 조회 오류 "));
				case SCHEDULE -> scheduleAccountRepo.findByAccount(
								transferTransactionHistoryDTO.getToAccountId())
						.orElseThrow(() -> new IllegalArgumentException("일정계좌 조회 오류"));
				case PERSONAL -> personalAccountRepo.findByAccount(
								transferTransactionHistoryDTO.getToAccountId())
						.orElseThrow(() -> new IllegalArgumentException("개인계좌 조회 오류"));
			};

			fromAccount.decreaseBalance(transferTransactionHistoryDTO.getAmount());
			toAccount.increaseBalance(transferTransactionHistoryDTO.getAmount());

			personalAccountRepo.save(fromAccount);

			if (toAccount instanceof PersonalAccount) {
				personalAccountRepo.save((PersonalAccount) toAccount);
			} else if (toAccount instanceof GatheringAccount) {
				gatheringAccountRepo.save((GatheringAccount) toAccount);
			} else {
				scheduleAccountRepo.save((ScheduleAccount) toAccount);
			}

			Trade trade = new Trade(
					null,
					fromAccount,
					AccountType.PERSONAL,
					toAccount,
					transferTransactionHistoryDTO.getToAccountType(),
					transferTransactionHistoryDTO.getAmount(),
					LocalDateTime.now(),
					transferTransactionHistoryDTO.getDetail(),
					fromAccount.getAccountBalance(),
					toAccount.getAccountBalance()
			);

			tradeRepo.save(trade);
			System.out.println(toAccount.getAccountNo());
			System.out.println(fromAccount.getAccountNo());
			BankTransferRequestDTO bankTransferRequestDTO = new BankTransferRequestDTO(
					fromAccount.getUser().getUserKey(),
					toAccount.getAccountNo(),
					fromAccount.getAccountNo(),
					transferTransactionHistoryDTO.getAmount()
			);

			externalBankApiComponent.sendTransferWithRetry(bankTransferRequestDTO);
			transferTransactionHistoryDTO.updateStatus(TransactionStatus.COMPLETED);

		} catch (Exception e) {
			e.printStackTrace();
			transferTransactionHistoryDTO.updateStatus(TransactionStatus.FAILED);
			return false;
		}

		return true;
	}

	public Boolean checkPassword(PasswordCheckRequestDTO requestDto) {
		// 1. 계좌번호로 계좌 정보 조회
		PersonalAccount account = personalAccountRepo.findByAccount(requestDto.getAccountId())
				.orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

		// 2. 저장된 암호화된 비밀번호와 입력된 비밀번호 비교
		String storedEncodedPassword = account.getAccountPw();

		// 3. BCrypt 비밀번호 검증
		return passwordEncoder.matches(String.valueOf(requestDto.getAccountPW()), storedEncodedPassword);
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

	public void addPersonalAccount(MakeExternalAccountDTO makeExternalAccountDTO, Long userId) {
		User user = userRepo.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("회원 조회 실패"));
		BankAccountResponseDTO responseDTO = externalBankApiComponent.externalMakeAccount(makeExternalAccountDTO);
		// 계좌 저장로직
		PersonalAccount personalAccount = new PersonalAccount(
				user,
				responseDTO.getRec().getAccountNo(),
				passwordEncoder.encode(String.valueOf(makeExternalAccountDTO.getAccountPw()))
		);
		personalAccountRepo.save(personalAccount);
	}

	public AccountCheckResponseDTO checkAccountNo(String toAccountNo, Long amount) {
		Boolean isAccountNo = accountRepo.existsByAccountNo(toAccountNo);
		return new AccountCheckResponseDTO(
				toAccountNo,
				amount,
				isAccountNo
		);
	}
}
