package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.dto.*;
import com.b110.jjeonchongmu.domain.account.dto.gatheringDTO.AccountCheckRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.account.dto.BankTransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.DeleteRequestDTO;
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
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.component.ExternalBankApiComponent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GatheringAccountService {
	private final ExternalBankApiComponent externalBankApiComponent;
	private final PersonalAccountRepo personalAccountRepo;
	private final GatheringAccountRepo gatheringAccountRepo;
	private final ScheduleAccountRepo scheduleAccountRepo;
	private final TradeRepo tradeRepo;
	private final AccountRepo accountRepo;
	private final UserRepo userRepo;
	private final GatheringRepo gatheringRepo;
	private final PasswordEncoder passwordEncoder;

	public Boolean checkPassword(PasswordCheckRequestDTO requestDto) {
		Long gatheringAccountId = requestDto.getAccountId();
		String gatheringAccountPW = requestDto.getAccountPW();
		// 비밀번호 확인 로직 구현
		GatheringAccount gatheringAccount =
				gatheringAccountRepo.findByAccount(gatheringAccountId)
						.orElseThrow(() -> new RuntimeException("Gathering Account Not Found"));

		if (passwordEncoder.matches(gatheringAccountPW, gatheringAccount.getAccountPw())) {
			return true;
		} else {
			return false;
		}
	}

	// 계좌 삭제
	public void deleteAccount(DeleteRequestDTO requestDTO) {
		// 계좌 삭제 로직 구현
		long gatheringAccountId = requestDTO.getAccountId();

		// jpa 메서드는 1 or 0 을 반환안해서 delete가 성공했는지는 log 를 받아와서 판단하는 로직이 필요함

		gatheringAccountRepo.deleteById(gatheringAccountId);
	}

	//    계좌 생성
	public boolean createAccount(Long userId, MakeAccountDTO requestDTO){
		User user = userRepo.findByUserId(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
		String scheduleAccountNo = "123-45-67891011";
		Long scheduleId = 2L;
		Gathering gathering = gatheringRepo.findById(scheduleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

		GatheringAccount gatheringAccount = new GatheringAccount(user,scheduleAccountNo,requestDTO.getAccountPw(),gathering);

		gatheringAccountRepo.save(gatheringAccount);
		return true;
	}
	@Transactional
	public TransferTransactionHistoryDTO initTransfer(TransferRequestDTO requestDto) {

		// 초기 송금기록
		return TransferTransactionHistoryDTO.builder()
				.fromAccountId(requestDto.getFromAccountId())
				.fromAccountType(requestDto.getFromAccountType())
				.toAccountId(requestDto.getToAccountId())
				.toAccountType(null)
				.amount(requestDto.getTransferAmount())
				.detail(requestDto.getTradeDetail())
				.status(TransactionStatus.BEFORE)
				.createdAt(LocalDateTime.now())
				.build();
	}

	@Transactional
	public boolean processTransfer(
			TransferTransactionHistoryDTO transferTransactionHistoryDTO) {

		try {

			transferTransactionHistoryDTO.updateStatus(TransactionStatus.PROCESSING);
			GatheringAccount fromAccount = gatheringAccountRepo.findByAccount(
							transferTransactionHistoryDTO.getToAccountId())
					.orElseThrow(() -> new IllegalArgumentException("입금 계좌를 가져오는중 오류발생"));

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
						.orElseThrow(() -> new IllegalArgumentException("모임계좌 조회 오류"));
				case SCHEDULE -> scheduleAccountRepo.findByAccount(
								transferTransactionHistoryDTO.getToAccountId())
						.orElseThrow(() -> new IllegalArgumentException("일정계좌 조회 오류"));
				case PERSONAL -> personalAccountRepo.findByAccount(
								transferTransactionHistoryDTO.getToAccountId())
						.orElseThrow(() -> new IllegalArgumentException("개인계좌 조회 오류"));
			};

			fromAccount.decreaseBalance(transferTransactionHistoryDTO.getAmount());
			toAccount.increaseBalance(transferTransactionHistoryDTO.getAmount());

			gatheringAccountRepo.save(fromAccount);

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
					fromAccount.getAccountBalance()
			);

			tradeRepo.save(trade);

			BankTransferRequestDTO bankTransferRequestDTO = new BankTransferRequestDTO(
					toAccount.getUser().getUserKey(),
					toAccount.getAccountNo(),
					fromAccount.getAccountNo(),
					transferTransactionHistoryDTO.getAmount()
			);

			externalBankApiComponent.sendTransferWithRetry(bankTransferRequestDTO);
			transferTransactionHistoryDTO.updateStatus(TransactionStatus.COMPLETED);

		} catch (Exception e) {
			transferTransactionHistoryDTO.updateStatus(TransactionStatus.FAILED);
			return false;
		}

		return true;
	}

	/**
	 *
	 * 여기서 userId는 누가 찾은지 보려고 필요함.
	 * 계좌 주인이랑 비교하는게 아님. 단순히 accountNo로 찾아오는 역할.
	 */

	public TransferRequestDTO getTransferRequestDto(Long userId, GatheringTransferRequestDTO transferRequestDto) {
		User user = userRepo.getUserByUserId(userId);
		Long groupId = transferRequestDto.getGroupId();
		String accountNo = transferRequestDto.getAccountNo();

		// 보내는 모임에서의 계좌를 찾는다.
		Gathering gathering;
		try {
			gathering = gatheringRepo.getGatheringByGatheringId(groupId);
		} catch (Exception e) {
			throw new RuntimeException("모임아이디로 모임을 찾을 수 없습니다");
		}
		AccountType fromAccountType = AccountType.GATHERING;
		Long fromAccountId = gathering.getGatheringAccount().getAccountId();

		Account account = accountRepo.findAccountByAccountNo(accountNo);
		AccountType toAccountType = account.getDtype();

		PersonalAccount personalAccount = personalAccountRepo.findByAccountNo(accountNo);
		Long toAccountId = personalAccount.getAccountId();

		String tradeDetail = transferRequestDto.getTradeDetail();

		Long transferAmount = transferRequestDto.getAmount();

		String accountPw = transferRequestDto.getAccountPw();

		return new TransferRequestDTO(
				fromAccountType,
				fromAccountId,
				toAccountType,
				toAccountId,
				tradeDetail,
				transferAmount,
				accountPw
		);
	}

	public String findNameByAccountNo(String accountNo) {
		Account account = accountRepo.findAccountByAccountNo(accountNo);
		return account.getUser().getName();
	}
}
