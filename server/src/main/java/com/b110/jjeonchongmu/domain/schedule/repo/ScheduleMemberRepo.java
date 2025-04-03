package com.b110.jjeonchongmu.domain.schedule.repo;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleMemberRepo extends JpaRepository<ScheduleMember, Long> {
    // 일정 멤버(참여자) 목록 조회
    List<ScheduleMember> findByScheduleId(Long scheduleId);

    // 일정 id 와 일정멤버 id로 scheduleMember 데이터 조회
    Optional<ScheduleMember> findByScheduleIdAndScheduleMemberUserId(Long scheduleId, Long userId);

    @Query("SELECT COUNT(sm) > 0 FROM ScheduleMember sm WHERE sm.scheduleMember.userId = :userId AND sm.schedule.gathering.gatheringId = :gatheringId")
    boolean existsByUserIdAndGatheringId(@Param("userId") Long userId, @Param("gatheringId") Long gatheringId);

    @Query("SELECT COUNT(sm) > 0 FROM ScheduleMember sm WHERE sm.scheduleMember.userId = :userId AND sm.schedule.id = :scheduleId")
    boolean existsByUserIdAndScheduleId(@Param("userId") Long userId, @Param("scheduleId") Long scheduleId);


    void deleteAllByScheduleId(Long scheduleId);


    @Query("SELECT COUNT(sm) FROM ScheduleMember sm WHERE sm.schedule.id = :scheduleId AND sm.isPenaltyApply = true")
    Integer countByScheduleIdAndPenaltyApplied(@Param("scheduleId") Long scheduleId);

    @Query("SELECT COUNT(sm) FROM ScheduleMember sm WHERE sm.schedule.id = :scheduleId AND sm.isPenaltyApply = false")
    Integer countByScheduleIdAndPenaltyNotApplied(@Param("scheduleId") Long scheduleId);

    Integer countByScheduleIdAndIsPenaltyApplyFalseOrIsPenaltyApplyIsNull(Long scheduleId);

    @Query("SELECT sm FROM ScheduleMember sm WHERE sm.schedule.id = :scheduleId AND sm.isPenaltyApply = false")
    List<ScheduleMember> findAllByScheduleIdAndPenaltyApplied(@Param("scheduleId") Long scheduleId);

    @Query("SELECT sm FROM ScheduleMember sm WHERE sm.schedule.id = :scheduleId AND sm.isPenaltyApply = true")
    List<ScheduleMember> findAllByScheduleIdAndPenaltyNotApplied(@Param("scheduleId") Long scheduleId);
}
