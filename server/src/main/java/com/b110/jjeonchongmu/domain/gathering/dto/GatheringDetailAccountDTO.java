package com.b110.jjeonchongmu.domain.gathering.dto;

import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringDetailAccountDTO {
    private Long groupBalance;
    private Long myBalance;
    private Long myDeposit;
    public GatheringDetailAccountDTO(Gathering gathering, GatheringMember gatheringMember) {
        this.groupBalance = gathering.getGatheringAccount().getAccountBalance();
        this.myBalance = gatheringMember.getGatheringMemberAccountBalance();
        this.myDeposit = gatheringMember.getGathering().getGatheringDeposit();
    }
}
