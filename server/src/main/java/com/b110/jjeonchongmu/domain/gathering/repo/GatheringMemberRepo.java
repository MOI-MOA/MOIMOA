package com.b110.jjeonchongmu.domain.gathering.repo;

import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDTO;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GatheringMemberRepo extends JpaRepository<GatheringMember, Long> {
    
    /**
     * 모임 ID로 모든 회원 정보 조회
     */
    List<GatheringMember> findByGatheringGatheringId(Long gatheringId);

    /**
     * 모임 ID와 사용자 ID로 회원 정보 조회
     */
    Optional<GatheringMember> findByGatheringGatheringIdAndGatheringMemberUserId(Long gatheringId, Long userId);

    /**
     * 사용자가 참여한 모든 모임의 회원 정보 조회
     */
    List<GatheringMember> findByGatheringMemberUserId(Long userId);

    /**
     * 모임 ID와 사용자 ID로 회원 정보 삭제
     */
    void deleteByGatheringGatheringIdAndGatheringMemberUserId(Long gatheringId, Long userId);

    /**
     * 모임 ID와 사용자 ID로 회원 존재 여부 확인
     */
    boolean existsByGatheringGatheringIdAndGatheringMemberUserId(Long gatheringId, Long userId);

    /**
     * 사용자 ID로 참여중인 모임 목록 조회
     */
    @Query("SELECT new com.b110.jjeonchongmu.domain.gathering.dto.GatheringDTO(" +
            "g.gatheringId, g.managerId, g.gatheringAccountId, g.gatheringName, " +
            "g.gatheringIntroduction, g.depositDate, g.basicFee, g.penaltyRate, " +
            "g.memberCount, g.gatheringDeposit) " +
            "FROM GatheringMember gm " +
            "JOIN gm.gathering g " +
            "WHERE gm.gatheringMemberUserId = :userId")
    List<GatheringDTO> findGatheringsByUserId(@Param("userId") Long userId);

    /**
     * 모임 ID로 회원 수 조회
     */
    long countByGatheringGatheringId(Long gatheringId);

    /**
     * 모임 ID로 납부 완료된 회원 수 조회
     */
    long countByGatheringGatheringIdAndGatheringPaymentStatusIsTrue(Long gatheringId);
} 