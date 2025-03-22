package com.b110.jjeonchongmu.domain.schedule.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.param.Param;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    
    @Query("SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDto(" +
           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, s.place, " +
           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
           "(SELECT COUNT(a) FROM ScheduleAttendee a WHERE a.schedule = s)) " +
           "FROM Schedule s")
    List<ScheduleListDto> findAllSchedules();

    @Query("SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDto(" +
           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, " +
           "new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDto$ManagerInfo(s.manager.id, s.manager.username), " +
           "s.startTime, s.place, SIZE(s.attendees), s.totalBudget, " +
           "EXISTS (SELECT 1 FROM ScheduleAttendee a WHERE a.schedule = s AND a.user.id = :userId), " +
           "(SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDto$AttendeeInfo(a.user.id, a.user.username) " +
           "FROM ScheduleAttendee a WHERE a.schedule = s)) " +
           "FROM Schedule s WHERE s.id = :scheduleId")
    Optional<ScheduleDetailDto> findScheduleDetailById(@Param("scheduleId") Long scheduleId, @Param("userId") Long userId);
}
