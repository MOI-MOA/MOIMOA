package com.b110.jjeonchongmu.domain.gathering.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 모임 관련 API 컨트롤러
 * 
 * [총무 권한]
 * 1. 모임 생성 - POST /api/v1/gathering
 * 2. 모임 수정 - PATCH /api/v1/gathering
 * 3. 모임 초대링크 생성 - POST /api/v1/gathering/{gatheringId}/invite
 * 4. 모임 참여 거절 - POST /api/v1/gathering/{gatheringId}/reject
 * 5. 멤버 삭제 - DELETE /api/v1/gathering/{gatheringId}/{userId}/member
 * 6. 모임 삭제 - DELETE /api/v1/gathering/{gatheringId}
 * 7. 모임 참여 수락 - POST /api/v1/gathering/{gatheringId}
 * 8. 모임 회원 관리 조회 - GET /api/v1/gathering/{gatheringId}/member-manage
 * 
 * [모든 사용자]
 * 9. 모임 회원 목록 조회 - GET /api/v1/gathering/{gatheringId}/members
 * 10. 모임 탈퇴하기 - DELETE /api/v1/gathering/{gatheringId}
 * 11. 전체 모임 목록 조회 - GET /api/v1/gathering
 * 12. 내 모임 조회 - GET /api/v1/gathering/my
 * 13. 모임 상세 조회 - GET /api/v1/gathering/{gatheringId}/detail
 * 14. 모임통장 거래내역 - GET /api/v1/gathering/{gatheringId}/trade
 */
@RestController
@RequestMapping("/api/v1/gathering")
@RequiredArgsConstructor
public class GatheringController {
    // TODO: 구현 예정
}
