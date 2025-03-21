package com.b110.jjeonchongmu.domain.gathering.repo;

import com.b110.jjeonchongmu.domain.entity.Gathering;
import com.b110.jjeonchongmu.domain.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GatheringMemberRepo extends JpaRepository<GatheringMember, Long> {
    
    /**
     * 사용자 ID로 해당 사용자가 참여한 모든 모임 정보를 조회
     */
    @Query("SELECT new com.b110.jjeonchongmu.domain.gathering.dto.GatheringDTO(" +
           "g.gatheringId, g.gatheringName, g.gatheringIntroduction, " +
           "g.memberCount, g.penaltyRate, g.depositDate, g.basicFee, g.gatheringDeposit) " +
           "FROM Gathering g " +
           "JOIN GatheringMember gm ON g.gatheringId = gm.gathering.gatheringId " +
           "WHERE gm.user.userId = :userId")
    List<GatheringDTO> findGatheringsByUserId(@Param("userId") Long userId);

    /**
     * 모임 ID와 사용자 ID로 모임 회원 정보 조회
     */
    GatheringMember findByGatheringGatheringIdAndUserUserId(Long gatheringId, Long userId);

    /**
     * 모임 ID로 모든 회원 정보 조회
     */
    List<GatheringMember> findByGatheringGatheringId(Long gatheringId);
} 