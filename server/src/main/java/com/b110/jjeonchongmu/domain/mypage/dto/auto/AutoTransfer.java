package com.b110.jjeonchongmu.domain.mypage.dto.auto;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoTransfer {
    private Long id; // 자동이체 id
    private int amount; // 가격
    private int day;
    private boolean status; // 활성여부

    private String account; // 모임계좌번호

    private Long deposit; // 보증금
    private String groupName; // 모임이름

    private int myBalance; // 잔액

    public AutoTransfer(Long userId, GatheringMember gatheringMember) {
        Gathering gathering = gatheringMember.getGathering();
        GatheringAccount gatheringAccount = gathering.getGatheringAccountId(); //일단 getGatheringAccountID로 했는데 나중에 고쳐
        List<AutoPayment> autoPayments = gatheringAccount.getAutoPayments();

        // 자동이체에서 뽑아오는 파트
        for (AutoPayment autoPayment : autoPayments) {
            if (autoPayment.getPersonalAccount().getPersonalAccountHolder().getUserId() == userId) {
                this.id = autoPayment.getAutoPaymentId();
                this.amount = autoPayment.getAutoPaymentAmount();
                this.day = autoPayment.getAutoPaymentDate();
                this.status = autoPayment.getIsActive();
                break;
            }
        }

        // 모임계좌에서 뽑아오는 파트
        this.account = gatheringAccount.getGatheringAccountNo();

        // 모임에서 뽑아오는 파트
        this.deposit = gathering.getGatheringDeposit();
        this.groupName = gathering.getGatheringName();

        // 모임멤버에서 뽑아오는 파트.
        this.myBalance = gatheringMember.getGatheringMemberAccountBalance();
    }
}
