package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.account.dto.AutoPaymentDTO;
import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.repo.AutoPaymentRepo;
import com.b110.jjeonchongmu.domain.account.repo.PersonalAccountRepo;
import com.b110.jjeonchongmu.domain.account.repo.GatheringAccountRepo;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.trade.entity.Trade;
import com.b110.jjeonchongmu.domain.trade.repo.TradeRepo;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AutoPaymentService {
    private final AutoPaymentRepo autoPaymentRepo;
    private final PersonalAccountRepo personalAccountRepo;
    private final GatheringAccountRepo gatheringAccountRepo;
    private final TradeRepo tradeRepo;

    /**
     * 모임 멤버에 대한 자동이체 생성
     */
    public void createAutoPaymentForGatheringMember(GatheringMember gatheringMember) {
        PersonalAccount personalAccount = personalAccountRepo.findByUserId(gatheringMember.getGatheringMemberUser().getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.PERSONAL_ACCOUNT_NOT_FOUND));
                
        GatheringAccount gatheringAccount = gatheringMember.getGathering().getGatheringAccount();
        
        // 이미 존재하는 자동이체 확인
        Optional<AutoPayment> existingAutoPayment = autoPaymentRepo
                .findByPersonalAccountAndGatheringAccount(personalAccount, gatheringAccount);
                
        if (existingAutoPayment.isPresent()) {
            AutoPayment autoPayment = existingAutoPayment.get();
            autoPayment.updateAutoPaymentAmount(gatheringMember.getGathering().getBasicFee());
            autoPayment.updateAutoPaymentDate(Integer.parseInt(gatheringMember.getGathering().getDepositDate()));
            autoPayment.updateIsActive(false);
            autoPaymentRepo.save(autoPayment);
            return;
        }

        // 새 자동이체 생성
        AutoPayment autoPayment = AutoPayment.builder()
                .personalAccount(personalAccount)
                .gatheringAccount(gatheringAccount)
                .autoPaymentAmount(gatheringMember.getGathering().getBasicFee())
                .autoPaymentDate(Integer.parseInt(gatheringMember.getGathering().getDepositDate()))
                .isActive(false)
                .build();

        autoPaymentRepo.save(autoPayment);
    }
    
    /**
     * 사용자의 모든 자동이체 목록 조회
     */
    @Transactional(readOnly = true)
    public List<AutoPaymentDTO> getAllAutoPaymentsByUserId(Long userId) {
        List<AutoPayment> autoPayments = autoPaymentRepo.findByUserId(userId);
        return autoPayments.stream()
                .map(AutoPaymentDTO::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 자동이체 활성화/비활성화
     */
    public void updateAutoPaymentStatus(Long autoPaymentId, Boolean isActive) {
        AutoPayment autoPayment = autoPaymentRepo.findById(autoPaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_PAYMENT_NOT_FOUND));
        
        autoPayment.updateIsActive(isActive);
        autoPaymentRepo.save(autoPayment);
    }
    
    /**
     * 자동이체 수정 (금액, 날짜)
     */
    public void updateAutoPayment(Long autoPaymentId, Long amount, Integer date) {
        AutoPayment autoPayment = autoPaymentRepo.findById(autoPaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_PAYMENT_NOT_FOUND));
        
        if (amount != null) {
            autoPayment.updateAutoPaymentAmount(amount);
        }
        
        if (date != null) {
            autoPayment.updateAutoPaymentDate(date);
        }
        
        autoPaymentRepo.save(autoPayment);
    }
    
    /**
     * 자동이체 삭제
     */
    public void deleteAutoPayment(Long autoPaymentId) {
        AutoPayment autoPayment = autoPaymentRepo.findById(autoPaymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_PAYMENT_NOT_FOUND));
        
        autoPaymentRepo.delete(autoPayment);
    }

    /**
     * 오늘 실행해야 할 자동이체 목록을 조회합니다.
     */
    public List<AutoPayment> getTodayAutoPayments() {
        LocalDate today = LocalDate.now();
        return autoPaymentRepo.findByIsActiveTrueAndAutoPaymentDate(today.getDayOfMonth());
    }

} 