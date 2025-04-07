package com.b110.jjeonchongmu.domain.mypage.dto.profile;

import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMemberStatus;
import com.b110.jjeonchongmu.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDefaultResponse {
    private String name; // 이름
    private String email; // 이메일
    private int joinedGroups; // 참여한 그룹 수
    private int totalBalance; // 그룹에 들어가 있는 돈
    public ProfileDefaultResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();

        int groupSize = 0;
        for (GatheringMember gatheringMember : user.getGatheringMembers()) {
            if (gatheringMember.getGatheringMemberStatus().equals(GatheringMemberStatus.ACTIVE)) {
                groupSize++;
            }
        }
        this.joinedGroups = groupSize;

        int sum = 0;
        for (GatheringMember member : user.getGatheringMembers()) {
            sum += member.getGatheringMemberAccountBalance();
        }
        this.totalBalance = sum;
    }
}