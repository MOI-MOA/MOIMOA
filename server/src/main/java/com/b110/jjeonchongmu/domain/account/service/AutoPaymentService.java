package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.repo.AutoPaymentRepo;
import com.b110.jjeonchongmu.domain.account.repo.PersonalAccountRepo;
import com.b110.jjeonchongmu.domain.account.repo.GatheringAccountRepo;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AutoPaymentService {
    private final AutoPaymentRepo autoPaymentRepo;
    private final PersonalAccountRepo personalAccountRepo;
    private final GatheringAccountRepo gatheringAccountRepo;

    public void createAutoPaymentForGatheringMember(GatheringMember gatheringMember) {
        
        PersonalAccount personalAccount = personalAccountRepo.findByUserId(gatheringMember.getGatheringMemberUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("개인 계좌를 찾을 수 없습니다."));
                
        GatheringAccount gatheringAccount = gatheringMember.getGathering().getGatheringAccount();
        
        // 이미 존재하는 자동이체 확인
        Optional<AutoPayment> existingAutoPayment = autoPaymentRepo
                .findByPersonalAccountAndGatheringAccount(personalAccount, gatheringAccount);
                
        if (existingAutoPayment.isPresent()) {
            AutoPayment autoPayment = existingAutoPayment.get();
            autoPayment.updateAutoPaymentAmount(gatheringMember.getGathering().getBasicFee());
            autoPayment.updateAutoPaymentDate(Integer.parseInt(gatheringMember.getGathering().getDepositDate()));
            autoPayment.updateIsActive(true);
            autoPaymentRepo.save(autoPayment);
            return;
        }

        // 새 자동이체 생성
        AutoPayment autoPayment = AutoPayment.builder()
                .personalAccount(personalAccount)
                .gatheringAccount(gatheringAccount)
                .autoPaymentAmount(gatheringMember.getGathering().getBasicFee())
                .autoPaymentDate(Integer.parseInt(gatheringMember.getGathering().getDepositDate()))
                .isActive(true)
                .build();

        autoPaymentRepo.save(autoPayment);
    }
} 