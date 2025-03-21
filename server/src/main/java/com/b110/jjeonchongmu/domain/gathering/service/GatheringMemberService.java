package com.b110.jjeonchongmu.domain.gathering.service;

import com.b110.jjeonchongmu.domain.gathering.dto.InviteResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.MemberListResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.MemberManageResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class GatheringMemberService {
    public InviteResponseDTO createInviteLink(Long gatheringId) {

        return null;
    }

    public void rejectGathering(Long gatheringId) {
    }

    public void deleteMember(Long gatheringId, Long userId) {
    }

    public void acceptGathering(Long gatheringId) {
    }

    public MemberManageResponseDTO getMemberManage(Long gatheringId) {
        return null;
    }

    public void leaveGathering(Long gatheringId) {
    }

    public MemberListResponseDTO getMembers(Long gatheringId) {
        return null;
    }
}
