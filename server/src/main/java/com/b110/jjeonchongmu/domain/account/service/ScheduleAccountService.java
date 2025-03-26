package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.dto.DeleteRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.MakeAccountDTO;
import com.b110.jjeonchongmu.domain.account.dto.PasswordCheckRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.account.repo.ScheduleAccountRepo;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleAccountService {

    private final ScheduleAccountRepo scheduleAccountRepo;
    private final TradeRepo tradeRepo;
    private final ScheduleRepo scheduleRepo;
    private final UserRepo userRepo;
//    계좌내역저장
    public Boolean initTransfer(TransferRequestDTO requestDto) {

    ScheduleAccount FromScheduleAccount = scheduleAccountRepo.findByAccount(requestDto.getFromAccountId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"FromScheduleAccount not found"));
    ScheduleAccount ToScheduleAccount = scheduleAccountRepo.findByAccount(requestDto.getToAccountId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"ToScheduleAccount not found"));
        ScheduleAccount scheduleAccount = new ScheduleAccount();

        Trade trade = Trade.builder()
                .fromAccount(FromScheduleAccount)
                .fromAccountType(requestDto.getFromAccountType())
                .toAccount(ToScheduleAccount)
                .toAccountType(ToScheduleAccount.getDtype())
                .tradeAmount(requestDto.getTransferAmount())
                .tradeTime(LocalDateTime.now())
                .tradeDetail(requestDto.getTradeDetail())
                .tradeBalance(FromScheduleAccount.getAccountBalance()-requestDto.getTransferAmount())
                .build();


        tradeRepo.save(trade);
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

        // jpa 메서드는 1 or 0 을 반환안해서 delete가 성공했는지는 log 를 받아와서 판단하는 로직이 필요함

        scheduleAccountRepo.deleteById(scheduleAccountId);
        return true;
    }
//    계좌 생성
    public boolean createAccount(MakeAccountDTO requestDTO){
        Long userId = 5L;
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        String scheduleAccountNo = "123-45-67891011";
        Long scheduleId = 2L;
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        ScheduleAccount scheduleAccount = new ScheduleAccount(user,scheduleAccountNo,requestDTO.getAccountPw(),schedule);

        scheduleAccountRepo.save(scheduleAccount);
        return true;
    }

}
