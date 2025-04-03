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

package com.b110.jjeonchongmu.domain.gathering.api;

import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.service.GatheringService;
import com.b110.jjeonchongmu.domain.trade.dto.TradeDTO;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import com.b110.jjeonchongmu.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.b110.jjeonchongmu.domain.gathering.dto.*;
import com.b110.jjeonchongmu.domain.gathering.service.*;

/**
 * 모임 관련 API 컨트롤러
 *
 * [총무 권한]
 * 1. 모임 생성 - POST /api/v1/gathering
 * 2. 모임 수정 - PATCH /api/v1/gathering
 * 3. 모임 삭제 - DELETE /api/v1/gathering/{gatheringId}
 * 4. 모임 상세 조회 - GET /api/v1/gathering/{gatheringId}/detail
 * 5. 전체 모임 목록 조회 - GET /api/v1/gathering
 * 6. 내 모임 조회 - GET /api/v1/gathering/my
 * 7. 모임통장 거래내역 - GET /api/v1/gathering/{gatheringId}/trade
 */
@RestController
@RequestMapping("/api/v1/gathering")
@RequiredArgsConstructor
public class GatheringController {
    private final GatheringService gatheringService;
    private final JwtUtil jwtUtil;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 모임 생성
     */
    @PostMapping
    public ResponseEntity<GatheringDTO> addGathering(@RequestBody AddGatheringDTO request) {
        GatheringDTO gatheringDTO= gatheringService.addGathering(request);
        return ResponseEntity.status(201).body(gatheringDTO);
    }

    /**
     * 모임 수정
     * 
     * @param request 모임 수정 정보
     * @return ResponseEntity<Gathering> 수정 결과
     */
    @PatchMapping
    public ResponseEntity<String> updateGathering(@RequestBody GatheringDTO request) {
        gatheringService.updateGathering(request.getGatheringId(), request);
        return ResponseEntity.status(201).body("업데이트 성공");
    }

    /**
     * 모임 삭제
     */
    @DeleteMapping("/{gatheringId}")
    public ResponseEntity<String> deleteGathering(@PathVariable Long gatheringId) {
        gatheringService.deleteGathering(gatheringId);
        return ResponseEntity.status(201).body("모임삭제성공");
    }

    /**
     * 전체 모임 목록 조회
     */
    @GetMapping()
    public ResponseEntity<GatheringListResponseDTO> getAllGatherings() {
        Long userId = jwtTokenProvider.getUserId();
        GatheringListResponseDTO response = gatheringService.getAllGatherings(userId);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * 내 모임 조회
     */
    @GetMapping("/my")
    public ResponseEntity<GatheringListResponseDTO> getMyGatherings() {
        Long userId = jwtTokenProvider.getUserId();
        GatheringListResponseDTO response = gatheringService.getMyGatherings(userId);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * 모임 상세 조회
     */
    @GetMapping("/{gatheringId}/detail")
    public ResponseEntity<GatheringDetailResponseDTO> getGatheringDetail(@PathVariable Long gatheringId) {
        Long userId = jwtTokenProvider.getUserId();
//        Long userId = 1L;
        GatheringDetailResponseDTO response = gatheringService.getGatheringDetail(userId, gatheringId);
        return ResponseEntity.status(201).body(response);
    }

//    /**
//     * 모임통장 거래내역
//     */
//    @GetMapping("/{gatheringId}/trade")
//    public ResponseEntity<Gathering> getGatheringTrades(@PathVariable Long gatheringId) {
//        TradeListResponseDTO response = gatheringService.getGatheringTrades(gatheringId);
//        return ResponseEntity.ok(new Gathering(200, "모임통장 거래내역 조회 성공", response));
//    }



}