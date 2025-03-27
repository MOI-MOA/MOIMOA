package com.b110.jjeonchongmu.domain.gathering.api;

import com.b110.jjeonchongmu.domain.gathering.dto.InviteResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.MemberListResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.MemberManageResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.service.GatheringMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 모임 회원 관련 API 컨트롤러
 * 
 * [총무 권한]
 * 1. 모임 초대링크 생성 - POST /api/v1/gathering/{gatheringId}/invite
 * 2. 모임 참여 거절 - POST /api/v1/gathering/{gatheringId}/reject/{userId}
 * 3. 멤버 삭제 - DELETE /api/v1/gathering/{gatheringId}/members/{userId}
 * 4. 모임 참여 수락 - POST /api/v1/gathering/{gatheringId}/accept/{userId}
 * 5. 모임 회원 관리 조회 - GET /api/v1/gathering/{gatheringId}/member-manage
 * 
 * [모든 사용자]
 * 6. 모임 회원 목록 조회 - GET /api/v1/gathering/{gatheringId}/members
 * 7. 모임 탈퇴하기 - DELETE /api/v1/gathering/{gatheringId}/leave
 */
@RestController
@RequestMapping("/api/v1/gathering")
@RequiredArgsConstructor
public class GatheringMemberController {
    private final GatheringMemberService gatheringMemberService;

    /**
     * 모임 초대링크 생성 (총무)
     */
    @PostMapping("/{gatheringId}/invite")
    public ResponseEntity<InviteResponseDTO> createInviteLink(@PathVariable Long gatheringId) {
        InviteResponseDTO response = gatheringMemberService.createInviteLink(gatheringId);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * 멤버 삭제 (총무)
     */
    @DeleteMapping("/{gatheringId}/members/{userId}")
    public ResponseEntity<String> deleteMember(
            @PathVariable Long gatheringId,
            @PathVariable Long userId) {
        gatheringMemberService.deleteMember(gatheringId, userId);
        return ResponseEntity.status(204).body("멤버삭제 성공");
    }

    /**
     * 모임 참여 수락 (총무)
     */
    @PostMapping("/{gatheringId}/accept/{userId}")
    public ResponseEntity<String> acceptGathering(
            @PathVariable Long gatheringId,
            @PathVariable Long userId) {
        gatheringMemberService.acceptGathering(gatheringId, userId);
        return ResponseEntity.status(201).body("모임 참여 수락");
    }

    /**
     * 모임 회원 관리 조회 (총무)
     */
    @GetMapping("/{gatheringId}/member-manage")
    public ResponseEntity<MemberManageResponseDTO> getMemberManage(@PathVariable Long gatheringId) {
        MemberManageResponseDTO response = gatheringMemberService.getMemberManage(gatheringId);
        return ResponseEntity.status(200).body(response);
    }

    /**
     * 모임 회원 목록 조회 (모든 사용자)
     */
    @GetMapping("/{gatheringId}/members")
    public ResponseEntity<MemberListResponseDTO> getMembers(@PathVariable Long gatheringId) {
        MemberListResponseDTO response = gatheringMemberService.getMembers(gatheringId);
        return ResponseEntity.status(200).body(response);
    }

    /**
     * 모임 탈퇴하기 (모든 사용자)
     */
    @DeleteMapping("/{gatheringId}/leave")
    public ResponseEntity<String> leaveGathering(@PathVariable Long gatheringId) {
        gatheringMemberService.leaveGathering(gatheringId);
        return ResponseEntity.status(204).body("모임탈퇴성공");
    }
}
