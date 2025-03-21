package com.b110.jjeonchongmu.domain.gathering.api;

import com.b110.jjeonchongmu.domain.gathering.dto.InviteResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.service.GatheringMemberService;
import com.b110.jjeonchongmu.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.b110.jjeonchongmu.domain.gathering.dto.*;

/**
 * 모임 회원 관련 API 컨트롤러
 * 
 * [총무 권한]
 * 1. 모임 초대링크 생성 - POST /api/v1/gathering/{gatheringId}/invite
 * 2. 모임 참여 거절 - POST /api/v1/gathering/{gatheringId}/reject/{userId}
 * 3. 멤버 삭제 - DELETE /api/v1/gathering/{gatheringId}/{userId}/member
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
     * 모임 초대링크 생성
     */
    @PostMapping("/{gatheringId}/invite")
    public ResponseEntity<ApiResponse> createInviteLink(@PathVariable Long gatheringId) {
        InviteResponseDTO response = gatheringMemberService.createInviteLink(gatheringId);
        return ResponseEntity.status(201).body(new ApiResponse(201, "모임 초대 링크 생성 성공", response));
    }

    /**
     * 모임 참여 거절
     */
    @PostMapping("/{gatheringId}/reject/{userId}")
    public ResponseEntity<ApiResponse> rejectGathering(@PathVariable Long gatheringId, @PathVariable Long userId) {
        gatheringMemberService.rejectGathering(gatheringId);
        return ResponseEntity.ok(new ApiResponse(200, "모임 참여 거절 성공"));
    }

    /**
     * 멤버 삭제
     */
    @DeleteMapping("/{gatheringId}/members/{userId}")
    public ResponseEntity<ApiResponse> deleteMember(@PathVariable Long gatheringId, @PathVariable Long userId) {
        gatheringMemberService.deleteMember(gatheringId, userId);
        return ResponseEntity.ok(new ApiResponse(200, "멤버 삭제 성공"));
    }

    /**
     * 모임 참여 수락
     */
    @PostMapping("/{gatheringId}/accept/{userId}")
    public ResponseEntity<ApiResponse> acceptGathering(@PathVariable Long gatheringId, @PathVariable Long userId) {
        gatheringMemberService.acceptGathering(gatheringId);
        return ResponseEntity.status(204).body(new ApiResponse(204, "모임 참여 수락 성공"));
    }

    /**
     * 모임 회원 관리 조회
     */
    @GetMapping("/{gatheringId}/member-manage")
    public ResponseEntity<ApiResponse> getMemberManage(@PathVariable Long gatheringId) {
        MemberManageResponseDTO response = gatheringMemberService.getMemberManage(gatheringId);
        return ResponseEntity.ok(new ApiResponse(200, "회원 관리 조회 성공", response));
    }

    /**
     * 모임 탈퇴하기
     */
    @DeleteMapping("/{gatheringId}/leave")
    public ResponseEntity<ApiResponse> leaveGathering(@PathVariable Long gatheringId) {
        gatheringMemberService.leaveGathering(gatheringId);
        return ResponseEntity.status(204).body(new ApiResponse(204, "모임 탈퇴 성공"));
    }

    /**
     * 모임 회원 목록 조회
     */
    @GetMapping("/{gatheringId}/members")
    public ResponseEntity<ApiResponse> getMembers(@PathVariable Long gatheringId) {
        MemberListResponseDTO response = gatheringMemberService.getMembers(gatheringId);
        return ResponseEntity.ok(new ApiResponse(200, "회원 목록 조회 성공", response));
    }
}
