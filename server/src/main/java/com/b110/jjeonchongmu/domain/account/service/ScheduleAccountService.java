package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.dto.DeleteRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.PasswordCheckRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.account.repo.ScheduleAccountRepo;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleAccountService {

    private final ScheduleAccountRepo scheduleAccountRepo;
    private final TradeRepo tradeRepo;

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

    public Boolean checkPassword(PasswordCheckRequestDTO requestDto) {
        Long scheduleAccountId = requestDto.getAccountId();
        String scheduleAccountPW = requestDto.getAccountPW();

        Optional<ScheduleAccount> storedAccount = scheduleAccountRepo.findByAccount(scheduleAccountId);

        return storedAccount
                .map(account -> account.getAccountPw().equals(scheduleAccountPW))
                .orElse(false);

    }

    public boolean deleteAccount(DeleteRequestDTO requestDTO) {
        long scheduleAccountId = requestDTO.getAccountId();

        // jpa 메서드는 1 or 0 을 반환안해서 delete가 성공했는지는 log 를 받아와서 판단하는 로직이 필요함

        scheduleAccountRepo.deleteById(scheduleAccountId);
        return true;
    }


}
