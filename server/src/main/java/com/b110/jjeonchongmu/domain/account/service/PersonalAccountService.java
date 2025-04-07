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
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringMemberRepo;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.component.ExternalBankApiComponent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final GatheringMemberRepo gatheringMemberRepo;
	private final AccountRepo accountRepo;
	private final TradeRepo tradeRepo;
	private final UserRepo userRepo;
	private static final Logger log = LoggerFactory.getLogger(PersonalAccountService.class);

	@Transactional
	public TransferTransactionHistoryDTO initTransfer(TransferRequestDTO requestDto) {

		Account account = null;

		account = personalAccountRepo.findByAccountNo(requestDto.getToAccountNo());
		if (account == null) {
			account = gatheringAccountRepo.findAccountByAccountNo(requestDto.getToAccountNo());
		}
		if (account == null) {
			try {
				throw new AccountNotFoundException("계좌를 찾을 수 없습니다: " + requestDto.getToAccountNo());
			} catch (AccountNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		// 초기 송금기록
		return TransferTransactionHistoryDTO.builder()
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
			TransferTransactionHistoryDTO transferTransactionHistoryDTO, Long userId) {
		try {
			transferTransactionHistoryDTO.updateStatus(TransactionStatus.PROCESSING);
			PersonalAccount fromAccount = personalAccountRepo.findByUserId(
							userId)
					.orElseThrow(() -> new IllegalArgumentException("입금 계좌를 가져오는중 오류발생"));

			String originAccountPw = fromAccount.getAccountPw();
			String userAccountPw = transferTransactionHistoryDTO.getAccountPw();
			boolean isPassword = passwordEncoder.matches(userAccountPw, originAccountPw);
			if (!isPassword) {
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
				GatheringAccount gatheringAccount = (GatheringAccount) toAccount;

				GatheringMember gatheringMember = gatheringMemberRepo.findByGatheringGatheringIdAndGatheringMemberUser_UserId(
								gatheringAccount.getGathering().getGatheringId(), userId)
						.orElseThrow(() -> new IllegalArgumentException("모임 멤버 조회 오류"));

				long gatheringRequiredDeposit = gatheringAccount.getGathering().getGatheringDeposit();
				long currentMemberDeposit = gatheringMember.getGatheringMemberAccountDeposit();
				long transferAmount = transferTransactionHistoryDTO.getAmount();

				if (currentMemberDeposit < gatheringRequiredDeposit) {
					long depositNeeded = gatheringRequiredDeposit - currentMemberDeposit;

					if (transferAmount <= depositNeeded) {
						// 이체 금액이 필요 보증금보다 적거나 같으면 모두 보증금으로 처리
						gatheringMember.updateGatheringMemberAccountDeposit(
								currentMemberDeposit + transferAmount);
					} else {
						// 필요한 보증금을 채우고 남은 금액은 잔액으로 처리
						gatheringMember.updateGatheringMemberAccountDeposit(gatheringRequiredDeposit);

						// 남은 금액 계산
						long remainingAmount = transferAmount - depositNeeded;
						// 잔액 업데이트
						gatheringMember.updateGatheringMemberAccountBalance(
								gatheringMember.getGatheringMemberAccountBalance() + remainingAmount);
					}
				} else {
					System.out.println(
							"여기? " + gatheringMember.getGatheringMemberAccountBalance() + transferAmount);
					gatheringMember.updateGatheringMemberAccountBalance(
							gatheringMember.getGatheringMemberAccountBalance() + transferAmount);
				}
				gatheringAccountRepo.save(gatheringAccount);
				gatheringMemberRepo.save(gatheringMember);
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

			String accountNo = null;
			if (toAccount instanceof PersonalAccount) {
				accountNo = ((PersonalAccount) toAccount).getAccountNo();
			} else if (toAccount instanceof GatheringAccount) {
				accountNo = ((GatheringAccount) toAccount).getAccountNo();
			}

			BankTransferRequestDTO bankTransferRequestDTO = new BankTransferRequestDTO(
					fromAccount.getUser().getUserKey(),
					accountNo,
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
		return passwordEncoder.matches(String.valueOf(requestDto.getAccountPW()),
				storedEncodedPassword);
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
		User user = userRepo.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("회원 조회 실패"));
		BankAccountResponseDTO responseDTO = externalBankApiComponent.externalMakeAccount(
				makeExternalAccountDTO);
		// 계좌 저장로직
		PersonalAccount personalAccount = new PersonalAccount(
				user,
				responseDTO.getRec().getAccountNo(),
				passwordEncoder.encode(String.valueOf(makeExternalAccountDTO.getAccountPw()))
		);
		personalAccountRepo.save(personalAccount); 
		
		// 300000만원 입금 - 외부 API 호출 부분을 try-catch로 감싸서 오류 처리
		try {
			// 외부 API 호출 전에 로그 추가
			log.info("외부 API 입금 처리 시작 - 사용자: {}, 계좌번호: {}", user.getUserKey(), responseDTO.getRec().getAccountNo());
			
			// 외부 API 호출
			externalBankApiComponent.externalDeposit(user.getUserKey(), responseDTO.getRec().getAccountNo());
			
			// 입금 성공 시 잔액 업데이트
			personalAccount.increaseBalance(300_000L);
			
			log.info("외부 API 입금 처리 완료 - 사용자: {}, 계좌번호: {}, 잔액: {}", 
					user.getUserKey(), responseDTO.getRec().getAccountNo(), personalAccount.getAccountBalance());
		} catch (Exception e) {
			log.error("외부 API 입금 처리 중 오류 발생: {}", e.getMessage());
			// 외부 API 호출 실패 시에도 계좌 생성은 계속 진행
		}
	}

	public AccountCheckResponseDTO checkAccountNo(String toAccountNo, Long amount) {
		Boolean isAccountNo = personalAccountRepo.existsByAccountNo(toAccountNo);
		if (!isAccountNo) {
			isAccountNo = gatheringAccountRepo.existsByAccountNo(toAccountNo);
		}
		return new AccountCheckResponseDTO(
				toAccountNo,
				amount,
				isAccountNo
		);
	}
}
