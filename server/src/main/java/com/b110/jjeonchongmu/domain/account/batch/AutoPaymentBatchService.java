package com.b110.jjeonchongmu.domain.account.batch;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.account.dto.BankTransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.repo.AutoPaymentRepo;
import com.b110.jjeonchongmu.domain.account.repo.PersonalAccountRepo;
import com.b110.jjeonchongmu.domain.account.repo.GatheringAccountRepo;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import com.b110.jjeonchongmu.global.component.ExternalBankApiComponent;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AutoPaymentBatchService {

    private final AutoPaymentRepo autoPaymentRepo;
    private final PersonalAccountRepo personalAccountRepo;
    private final GatheringAccountRepo gatheringAccountRepo;
    private final TradeRepo tradeRepo;
    private final ExternalBankApiComponent externalBankApiComponent;

    /**
     * 자동이체 배치 작업을 처리합니다.
     */
    public void processAutoPayments() {
        log.info("자동이체 배치 작업 시작");
        
        // 오늘 실행해야 할 자동이체 목록 조회
        LocalDate today = LocalDate.now();
        List<AutoPayment> todayAutoPayments = autoPaymentRepo.findByIsActiveTrueAndAutoPaymentDate(today.getDayOfMonth());
        
        for (AutoPayment autoPayment : todayAutoPayments) {
            try {
                // 자동이체 실행
                executeAutoPayment(autoPayment);
                log.info("자동이체 성공 - ID: {}, 금액: {}", 
                        autoPayment.getAutoPaymentId(), 
                        autoPayment.getAmount());
            } catch (Exception e) {
                log.error("자동이체 실패 - ID: {}, 오류: {}", 
                        autoPayment.getAutoPaymentId(), 
                        e.getMessage());
            }
        }
        
        log.info("자동이체 배치 작업 완료");
    }

    /**
     * 자동이체를 실행합니다.
     * 개인계좌에서 모임계좌로의 자동이체만 처리합니다.
     */
    @Transactional
    public void executeAutoPayment(AutoPayment autoPayment) throws IllegalAccessException {
        // 출금 계좌와 입금 계좌 조회
        PersonalAccount fromAccount = autoPayment.getPersonalAccount();
        GatheringAccount toAccount = autoPayment.getGatheringAccount();
        
        // 잔액 확인
        if (fromAccount.getAccountBalance() < autoPayment.getAmount()) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        
        // 계좌 잔액 업데이트
        fromAccount.decreaseBalance(autoPayment.getAmount());
        toAccount.increaseBalance(autoPayment.getAmount());

        // 계좌 정보 저장
        personalAccountRepo.save(fromAccount);
        gatheringAccountRepo.save(toAccount);

        // 거래 내역 저장
        Trade trade = new Trade(
            null,
            fromAccount,
            AccountType.PERSONAL,
            toAccount,
            AccountType.GATHERING,
            autoPayment.getAmount(),
            LocalDateTime.now(),
            "자동이체",
            fromAccount.getAccountBalance(),
            toAccount.getAccountBalance()
        );
        tradeRepo.save(trade);

        // 외부 은행 API 호출
        BankTransferRequestDTO bankTransferRequestDTO = new BankTransferRequestDTO(
            fromAccount.getUser().getUserKey(),
            toAccount.getAccountNo(),
            fromAccount.getAccountNo(),
            autoPayment.getAmount()
        );
        externalBankApiComponent.sendTransferWithRetry(bankTransferRequestDTO);

        log.info("자동이체 완료 - 출금계좌: {}, 입금계좌: {}, 금액: {}", 
                fromAccount.getAccountId(), 
                toAccount.getAccountId(), 
                autoPayment.getAmount());
    }
} 