package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.dto.*;
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
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.component.ExternalBankApiComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
//@Transactional
public class ScheduleAccountService {

    private final ScheduleAccountRepo scheduleAccountRepo;
    private final TradeRepo tradeRepo;
    private final ScheduleRepo scheduleRepo;
    private final UserRepo userRepo;
    private final GatheringAccountRepo gatheringAccountRepo;
    private final PersonalAccountRepo personalAccountRepo;
    private final PasswordEncoder passwordEncoder;
    private final ExternalBankApiComponent externalBankApiComponent;
    private final AccountRepo accountRepo;
//    계좌내역저장
    public TransferTransactionHistoryDTO initTransfer(TransferRequestDTO requestDto) {



        Account ToAccount = null;
        if(requestDto.getToAccountType()==AccountType.PERSONAL){
            ToAccount = personalAccountRepo.findByAccountNo(requestDto.getToAccountNo());
        } else if (requestDto.getToAccountType()==AccountType.GATHERING){
            ToAccount = gatheringAccountRepo.findAccountByAccountNo(requestDto.getToAccountNo());
        }


        return TransferTransactionHistoryDTO.builder()
                .fromAccountId(requestDto.getFromAccountId())
                .fromAccountType(requestDto.getFromAccountType())
                .toAccountId(ToAccount.getAccountId())
                .toAccountType(ToAccount.getDtype())
                .amount(requestDto.getTransferAmount())
                .detail(requestDto.getTradeDetail())
                .status(TransactionStatus.BEFORE)
                .createdAt(LocalDateTime.now())
                .accountPw(requestDto.getAccountPw())
                .build();
    }

    public boolean processTransfer(
            TransferTransactionHistoryDTO transferTransactionHistoryDTO) {

        try {

            transferTransactionHistoryDTO.updateStatus(TransactionStatus.PROCESSING);
            ScheduleAccount fromAccount = scheduleAccountRepo.findByAccount(
                            transferTransactionHistoryDTO.getToAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("입금 계좌를 가져오는중 오류발생"));

            GatheringAccount fromGatheringAccount = fromAccount.getSchedule().getGathering().getGatheringAccount();

            String originAccountPw = fromAccount.getAccountPw();
            String scheduleAccountPw = transferTransactionHistoryDTO.getAccountPw();
            System.out.println(originAccountPw + " , " + scheduleAccountPw);
            boolean isPassword = passwordEncoder.matches(scheduleAccountPw, originAccountPw);
            if(!isPassword) {
                throw new IllegalAccessException("비밀번호 불일치");
            }

            // 잔액 검증
            if (fromAccount.getAccountBalance() < transferTransactionHistoryDTO.getAmount()) {
                transferTransactionHistoryDTO.updateStatus(TransactionStatus.FAILED);
                throw new IllegalStateException("잔액이 부족합니다");
            }

            // 계좌 타입에 따라 입금 계좌 조회

            Account toAccount = null;
            String accountNo = null;
            GatheringAccount toGatheringAccount;
            PersonalAccount toPersonalAccount;
            
            switch (transferTransactionHistoryDTO.getToAccountType()) {
                case GATHERING -> {
                    toGatheringAccount = gatheringAccountRepo.findByAccount(
                                    transferTransactionHistoryDTO.getToAccountId())
                            .orElseThrow(() -> new IllegalArgumentException("모임계좌 조회 오류 "));
                    toAccount = toGatheringAccount;
                    accountNo = toGatheringAccount.getAccountNo();
                }
            
                case PERSONAL -> {
                    toPersonalAccount = personalAccountRepo.findByAccount(
                                    transferTransactionHistoryDTO.getToAccountId())
                            .orElseThrow(() -> new IllegalArgumentException("모임계좌 조회 오류 "));
                    toAccount = toPersonalAccount;
                    accountNo = toPersonalAccount.getAccountNo();
                }
            };

            fromAccount.decreaseBalance(transferTransactionHistoryDTO.getAmount()); // 일정계좌 금액 차감
            fromGatheringAccount.decreaseBalance(transferTransactionHistoryDTO.getAmount()); // 모임계좌도 금액 차감
            toAccount.increaseBalance(transferTransactionHistoryDTO.getAmount());

            scheduleAccountRepo.save(fromAccount);

            if (toAccount instanceof PersonalAccount) {
                personalAccountRepo.save((PersonalAccount) toAccount);

            } else if  (toAccount instanceof  GatheringAccount){
                gatheringAccountRepo.save((GatheringAccount) toAccount);
            }

            Trade trade = new Trade(
                    null,
                    fromGatheringAccount,
                    AccountType.GATHERING,
                    toAccount,
                    transferTransactionHistoryDTO.getToAccountType(),
                    transferTransactionHistoryDTO.getAmount(),
                    LocalDateTime.now(),
                    transferTransactionHistoryDTO.getDetail(),
                    fromAccount.getAccountBalance(),
                    toAccount.getAccountBalance()
            );
            tradeRepo.save(trade);
            // 거래내역 DB에 저장

            transferTransactionHistoryDTO.getToAccountId();

            BankTransferRequestDTO bankTransferRequestDTO = new BankTransferRequestDTO(
                    toAccount.getUser().getUserKey(),
                    accountNo,
                    fromAccount.getSchedule().getGathering().getGatheringAccount().getAccountNo(),
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


//    비밀번호 체크
    public Boolean checkPassword(PasswordCheckRequestDTO requestDto) {
        Long scheduleAccountId = requestDto.getAccountId();
        String scheduleAccountPW = requestDto.getAccountPW();

        Optional<ScheduleAccount> storedAccount = scheduleAccountRepo.findByAccount(scheduleAccountId);

        return storedAccount
                .map(account -> account.getAccountPw().equals(scheduleAccountPW))
                .orElse(false);
    }

//    계좌삭제
public boolean deleteAccount(DeleteRequestDTO requestDTO) {
    long scheduleAccountId = requestDTO.getAccountId();
    // 삭제 대상이 존재하는지 확인
    boolean isExist = scheduleAccountRepo.existsById(scheduleAccountId);
    scheduleAccountRepo.deleteById(scheduleAccountId);
    return isExist;
}

//    계좌 생성
    public boolean createAccount(Long userId,Long scheduleId,MakeAccountDTO requestDTO,Long perBudget){

        try{
            User user = userRepo.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
            Schedule schedule = scheduleRepo.findById(scheduleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));


            ScheduleAccount scheduleAccount = new ScheduleAccount(user,requestDTO.getAccountPw(),schedule,perBudget);
            // 부총무 한명의 인당예산만큼 잔액 추가



            scheduleAccountRepo.save(scheduleAccount);
            schedule.updateScheduleAccount(scheduleAccount);
            scheduleRepo.save(schedule);

            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

}
