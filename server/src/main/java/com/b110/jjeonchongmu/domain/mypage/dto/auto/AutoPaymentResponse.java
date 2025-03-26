package com.b110.jjeonchongmu.domain.mypage.dto.auto;

import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoPaymentResponse {
    private Long accountBalance;
    private List<AutoPaymentDto> autoTransfers = new ArrayList<>();
    public AutoPaymentResponse(User user) {
        this.accountBalance = user.getPersonalAccount().getAccountBalance();
        for (GatheringMember gatheringMember : user.getGatheringMembers()) {
            this.autoTransfers.add(new AutoPaymentDto(user.getUserId(), gatheringMember));
        }
    }
}