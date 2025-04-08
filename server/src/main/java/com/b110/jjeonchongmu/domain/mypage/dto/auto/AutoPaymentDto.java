package com.b110.jjeonchongmu.domain.mypage.dto.auto;

import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoPaymentDto {
    private Long id; // 자동이체 id
    private Long amount; // 가격
    private Integer day;
    private boolean status; // 활성여부
    private String account; // 모임계좌번호
    private Long deposit; // 보증금
    private String groupName; // 모임이름
    private Long myBalance; // 잔액
    private boolean paymentStatus;

    public AutoPaymentDto(Long userId, GatheringMember gatheringMember) {
        Gathering gathering = gatheringMember.getGathering();
        GatheringAccount gatheringAccount = gathering.getGatheringAccount(); //일단 getGatheringAccountID로 했는데 나중에 고쳐

        // 모임에서 뽑아오는 파트
        this.deposit = gathering.getGatheringDeposit();
        this.groupName = gathering.getGatheringName();
        // 모임멤버에서 뽑아오는 파트.
        this.myBalance = gatheringMember.getGatheringMemberAccountBalance();
    }
}
