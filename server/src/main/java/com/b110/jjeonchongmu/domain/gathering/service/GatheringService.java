package com.b110.jjeonchongmu.domain.gathering.service;

import com.b110.jjeonchongmu.domain.gathering.dto.*;
import com.b110.jjeonchongmu.domain.entity.Gathering;
import com.b110.jjeonchongmu.domain.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringMemberRepo;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringService {
    private final GatheringRepo gatheringRepo;
    private final GatheringMemberRepo gatheringMemberRepo;

    /**
     * 모임 생성
     */
    @Transactional
    public void addGathering(GatheringDTO request) {
        Gathering gathering = Gathering.builder()
                .gatheringName(request.getGatheringName())
                .gatheringIntroduction(request.getGatheringIntroduction())
                .memberCount(request.getMemberCount())
                .penaltyRate(request.getPenaltyRate())
                .depositDate(request.getDepositDate())
                .basicFee(request.getBasicFee())
                .gatheringDeposit(request.getGatheringDeposit())
                .build();
        
        gatheringRepo.save(gathering);
    }

//    /**
//     * 모임 수정
//     */
//    @Transactional
//    public void updateGathering(GatheringDTO request) {
//        Gathering gathering = gatheringRepo.findById(request.getGatheringId())
//                .orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));
//
//        gathering.updateGathering(
//                request.getGatheringName(),
//                request.getGatheringIntroduction(),
//                request.getMemberCount(),
//                request.getPenaltyRate(),
//                request.getDepositDate(),
//                request.getBasicFee()
//        );
//    }

    /**
     * 모임 삭제
     */
    @Transactional
    public void deleteGathering(Long gatheringId) {
        if (!gatheringRepo.existsById(gatheringId)) {
            throw new CustomException(ErrorCode.GATHERING_NOT_FOUND);
        }
        gatheringRepo.deleteById(gatheringId);
    }

    /**
     * 전체 모임 목록 조회
     */
    public GatheringListResponseDTO getAllGatherings() {
        List<Gathering> gatherings = gatheringRepo.findAll();
        return convertToGatheringListResponse(gatherings);
    }

    /**
     * 내 모임 조회
     */
    public GatheringListResponseDTO getMyGatherings(Long userId) {
        List<GatheringDTO> gatherings = gatheringMemberRepo.findGatheringsByUserId(userId);
        return GatheringListResponseDTO.builder()
                .gatherings(gatherings)
                .build();
    }

    /**
     * 모임 상세 조회
     */
    public GatheringDetailResponseDTO getGatheringDetail(Long gatheringId) {
        Gathering gathering = gatheringRepo.findById(gatheringId)
                .orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));
        
        List<GatheringMember> members = gatheringMemberRepo.findByGatheringGatheringId(gatheringId);

        return GatheringDetailResponseDTO.builder()
                .gathering(GatheringDTO.builder()
                        .gatheringId(gathering.getGatheringId())
                        .gatheringName(gathering.getGatheringName())
                        .gatheringIntroduction(gathering.getGatheringIntroduction())
                        .memberCount(gathering.getMemberCount())
                        .penaltyRate(gathering.getPenaltyRate())
                        .depositDate(gathering.getDepositDate())
                        .basicFee(gathering.getBasicFee())
                        .gatheringDeposit(gathering.getGatheringDeposit())
                        .build())
                .members(members.stream()
                        .map(member -> GatheringMemberDTO.builder()
                                .name(member.getName())
                                .email(member.getEmail())
                                .createdAt(member.getCreatedAt())
                                .gatheringPaymentStatus(member.isGatheringPaymentStatus())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 모임통장 거래내역 조회
     */
    public TradeListResponseDTO getGatheringTrades(Long gatheringId) {
        Gathering gathering = gatheringRepo.findById(gatheringId)
                .orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));
        
        // TODO: 거래내역 조회 로직 구현
        return null;
    }

    private GatheringListResponseDTO convertToGatheringListResponse(List<Gathering> gatherings) {
        List<GatheringDTO> gatheringDTOs = gatherings.stream()
                .map(gathering -> GatheringDTO.builder()
                        .gatheringId(gathering.getGatheringId())
                        .gatheringName(gathering.getGatheringName())
                        .gatheringIntroduction(gathering.getGatheringIntroduction())
                        .memberCount(gathering.getMemberCount())
                        .penaltyRate(gathering.getPenaltyRate())
                        .depositDate(gathering.getDepositDate())
                        .basicFee(gathering.getBasicFee())
                        .gatheringDeposit(gathering.getGatheringDeposit())
                        .build())
                .collect(Collectors.toList());

        return GatheringListResponseDTO.builder()
                .gatherings(gatheringDTOs)
                .build();
    }
}