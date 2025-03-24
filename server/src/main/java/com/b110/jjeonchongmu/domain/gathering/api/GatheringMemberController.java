package com.b110.jjeonchongmu.domain.gathering.api;

import com.b110.jjeonchongmu.domain.gathering.dto.InviteResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.MemberListResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.MemberManageResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.service.GatheringMemberService;
import com.b110.jjeonchongmu.global.common.ApiResponse;
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
    public ResponseEntity<ApiResponse> createInviteLink(@PathVariable Long gatheringId) {
        InviteResponseDTO response = gatheringMemberService.createInviteLink(gatheringId);
        return ResponseEntity.status(201).body(new ApiResponse(201, "모임 초대 링크 생성 성공", response));
    }

    /**
     * 모임 참여 거절 (총무)
     */
    @PostMapping("/{gatheringId}/reject/{userId}")
    public ResponseEntity<ApiResponse> rejectGathering(
            @PathVariable Long gatheringId,
            @PathVariable Long userId) {
        gatheringMemberService.rejectGathering(gatheringId, userId);
        return ResponseEntity.status(201).body(new ApiResponse(201, "모임 참여 거절 성공"));
    }

    /**
     * 멤버 삭제 (총무)
     */
    @DeleteMapping("/{gatheringId}/members/{userId}")
    public ResponseEntity<ApiResponse> deleteMember(
            @PathVariable Long gatheringId,
            @PathVariable Long userId) {
        gatheringMemberService.deleteMember(gatheringId, userId);
        return ResponseEntity.status(204).body(new ApiResponse(204, "멤버 삭제 성공"));
    }

    /**
     * 모임 참여 수락 (총무)
     */
    @PostMapping("/{gatheringId}/accept/{userId}")
    public ResponseEntity<ApiResponse> acceptGathering(
            @PathVariable Long gatheringId,
            @PathVariable Long userId) {
        gatheringMemberService.acceptGathering(gatheringId, userId);
        return ResponseEntity.status(201).body(new ApiResponse(201, "모임 참여 수락 성공"));
    }

    /**
     * 모임 회원 관리 조회 (총무)
     */
    @GetMapping("/{gatheringId}/member-manage")
    public ResponseEntity<ApiResponse> getMemberManage(@PathVariable Long gatheringId) {
        MemberManageResponseDTO response = gatheringMemberService.getMemberManage(gatheringId);
        return ResponseEntity.ok(new ApiResponse(200, "회원 관리 조회 성공", response));
    }

    /**
     * 모임 회원 목록 조회 (모든 사용자)
     */
    @GetMapping("/{gatheringId}/members")
    public ResponseEntity<ApiResponse> getMembers(@PathVariable Long gatheringId) {
        MemberListResponseDTO response = gatheringMemberService.getMembers(gatheringId);
        return ResponseEntity.ok(new ApiResponse(200, "회원 목록 조회 성공", response));
    }

    /**
     * 모임 탈퇴하기 (모든 사용자)
     */
    @DeleteMapping("/{gatheringId}/leave")
    public ResponseEntity<ApiResponse> leaveGathering(@PathVariable Long gatheringId) {
        gatheringMemberService.leaveGathering(gatheringId);
        return ResponseEntity.status(204).body(new ApiResponse(204, "모임 탈퇴 성공"));
    }
}
