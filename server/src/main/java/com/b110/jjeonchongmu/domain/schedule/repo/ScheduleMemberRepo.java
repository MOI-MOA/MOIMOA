package com.b110.jjeonchongmu.domain.schedule.repo;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleMemberRepo extends JpaRepository<ScheduleMember, Long> {
    // 일정 멤버(참여자) 목록 조회
    List<ScheduleMember> findByScheduleIdAndIsAttendTrue(Long scheduleId);

    // 일정 id 와 일정멤버 id로 scheduleMember 데이터 조회
    Optional<ScheduleMember> findByScheduleIdAndScheduleMemberUserIdAndIsAttendTrue(Long scheduleId, Long userId);

    @Query("SELECT COUNT(sm) > 0 FROM ScheduleMember sm WHERE sm.scheduleMember.userId = :userId AND sm.schedule.id = :scheduleId AND sm.isAttend = true")
    boolean existsByUserIdAndScheduleIdIsAttendTrue(@Param("userId") Long userId, @Param("scheduleId") Long scheduleId);


    void deleteAllByScheduleId(Long scheduleId);


    // 일정 참여 X 페널티 대상자
    @Query("SELECT COUNT(sm) FROM ScheduleMember sm WHERE sm.schedule.id = :scheduleId AND sm.isPenaltyApply = true And sm.isAttend = false")
    Integer countByScheduleIdAndPenaltyAppliedIsAttendFalse(@Param("scheduleId") Long scheduleId);

    // 일정 참여 O 페널티 미대상자
    @Query("SELECT COUNT(sm) FROM ScheduleMember sm WHERE sm.schedule.id = :scheduleId AND sm.isPenaltyApply = false And sm.isAttend = true")
    Integer countByScheduleIdAndPenaltyNotAppliedIsAttendTrue(@Param("scheduleId") Long scheduleId);

}
