package com.b110.jjeonchongmu.domain.gathering.repo;

import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDTO;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMemberStatus;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.user.entity.User;
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
     * 모임 ID와 사용자 ID로 회원 정보 삭제
     */
    void deleteByGatheringGatheringIdAndGatheringMemberUser_UserId(Long gatheringId, Long userId);

    /**
     * 모임 ID와 사용자 ID로 회원 존재 여부 확인
     */
    boolean existsByGatheringGatheringIdAndGatheringMemberUser_UserId(Long gatheringId, Long userId);

    /**
     * 사용자 ID로 참여중인 모임 목록 조회
     */
    @Query("SELECT new com.b110.jjeonchongmu.domain.gathering.dto.GatheringDTO(" +
            "g.gatheringId, g.manager.userId, g.gatheringAccount.accountId, g.gatheringName, " +
            "g.gatheringIntroduction, g.depositDate, g.basicFee, g.penaltyRate, " +
            "g.memberCount, g.gatheringDeposit) " +
            "FROM GatheringMember gm " +
            "JOIN gm.gathering g " +
            "WHERE gm.gatheringMemberUser.userId = :userId")
    List<GatheringDTO> findGatheringsByUserId(@Param("userId") Long userId);

    /**
     * 모임 ID로 회원 수 조회
     */
    long countByGatheringGatheringId(Long gatheringId);

    @Query("SELECT COUNT(gm) > 0 FROM GatheringMember gm WHERE gm.gatheringMemberUser.userId = :userId AND gm.gathering.gatheringId = :gatheringId")
    boolean existsByUserIdAndGatheringId(@Param("userId") Long userId, @Param("gatheringId") Long gatheringId);

    @Query("" +
            "SELECT gm " +
            "FROM GatheringMember gm " +
            "WHERE gm.gatheringMemberUser.userId = :userId " +
            "AND gm.gathering.gatheringId = :gatheringId")
    Optional<GatheringMember> getGatheringMemberByGatheringIdAndUserId(@Param("gatheringId") Long gatheringId, @Param("userId") Long userId);

    Optional<GatheringMember> findByGatheringGatheringIdAndGatheringMemberUser_UserId(Long gatheringId, Long userId);

    long countByGatheringGatheringIdAndGatheringMemberStatus(Long gatheringId, GatheringMemberStatus status);

    List<GatheringMember> findByGatheringGatheringIdAndGatheringMemberStatus(Long gatheringId, GatheringMemberStatus status);

    @Query("SELECT gm FROM GatheringMember gm " +
            "JOIN gm.gathering g " +
            "JOIN g.schedules s " +
            "JOIN s.attendees sm " +
            "WHERE s.id = :scheduleId " +
            "AND sm.isPenaltyApply = TRUE AND sm.isAttend = FALSE")
    List<GatheringMember> findGatheringMembersByScheduleIdAndPenaltyAppliedIsAttendFalse(@Param("scheduleId") Long scheduleId);


    @Query("SELECT gm FROM GatheringMember gm " +
            "JOIN gm.gathering g " +
            "JOIN g.schedules s " +
            "JOIN s.attendees sm " +
            "WHERE s.id = :scheduleId " +
            "AND sm.isPenaltyApply = FALSE AND sm.isAttend=true")
    List<GatheringMember> findGatheringMembersByScheduleIdAndPenaltyNotAppliedIsAttendTrue(@Param("scheduleId") Long scheduleId);

    @Query("SELECT gm FROM GatheringMember gm " +
            "JOIN gm.gathering g " +
            "JOIN g.schedules s " +
            "WHERE s.id = :scheduleId AND gm.gatheringMemberUser.userId = :userId")
    GatheringMember findGatheringMemberByScheduleIdAndUserId(
            @Param("scheduleId") Long scheduleId,
            @Param("userId") Long userId);

    Optional<GatheringMember> findByGatheringAndGatheringMemberUser(Gathering gathering, User user);
}