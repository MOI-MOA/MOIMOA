package com.b110.jjeonchongmu.domain.schedule.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDTO;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDTO;

public interface ScheduleRepo extends JpaRepository<ScheduleListDTO, Long> {
    
//    @Query("SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDTO(" +
//           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, s.place, " +
//           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
//           "(SELECT COUNT(a) FROM ScheduleAttendee a WHERE a.schedule = s)) " +
//           "FROM Schedule s")
//    List<ScheduleListDTO> findAllSchedules();
//
//    @Query("SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDTO(" +
//           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, " +
//           "new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDto$ManagerInfo(s.manager.id, s.manager.username), " +
//           "s.startTime, s.place, SIZE(s.attendees), s.totalBudget, " +
//           "EXISTS (SELECT 1 FROM ScheduleAttendee a WHERE a.schedule = s AND a.user.id = :userId), " +
//           "(SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDto$AttendeeInfo(a.user.id, a.user.username) " +
//           "FROM ScheduleAttendee a WHERE a.schedule = s)) " +
//           "FROM Schedule s WHERE s.id = :scheduleId")
//    Optional<ScheduleDetailDTO> findScheduleDetailById(@Param("scheduleId") Long scheduleId, @Param("userId") Long userId);
}
