package com.b110.jjeonchongmu.domain.main.repo;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainRepo extends JpaRepository<Schedule, Long> {
//
//    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.checked = false")
//    int countUncheckSchedules();
//
//    @Query("SELECT DISTINCT DAY(s.scheduleStartTime) FROM Schedule s " +
//           "WHERE YEAR(s.scheduleStartTime) = YEAR(CURRENT_DATE) " +
//           "AND MONTH(s.scheduleStartTime) = MONTH(CURRENT_DATE)")
//    List<Integer> findCurrentMonthDates();
//
//    @Query("SELECT new com.b110.jjeonchongmu.domain.main.dto.response.ScheduleDto(" +
//           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, s.place, " +
//           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
//           "(SELECT COUNT(a) FROM Attendee a WHERE a.schedule = s)) " +
//           "FROM Schedule s " +
//           "WHERE DATE(s.scheduleStartTime) = CURRENT_DATE")
//    List<ScheduleDto> findTodaySchedules();
//
//    @Query("SELECT new com.b110.jjeonchongmu.domain.main.dto.response.ScheduleDto(" +
//           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, s.place, " +
//           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
//           "(SELECT COUNT(a) FROM Attendee a WHERE a.schedule = s)) " +
//           "FROM Schedule s " +
//           "WHERE s.scheduleStartTime > CURRENT_DATE " +
//           "ORDER BY s.scheduleStartTime")
//    List<ScheduleDto> findUpcomingSchedules();
//
//    @Query("SELECT new com.b110.jjeonchongmu.domain.main.dto.response.ScheduleDto(" +
//           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, s.place, " +
//           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
//           "(SELECT COUNT(a) FROM Attendee a WHERE a.schedule = s)) " +
//           "FROM Schedule s " +
//           "WHERE s.checked = false")
//    List<ScheduleDto> findUncheckSchedules();
//
//    @Query("SELECT new com.b110.jjeonchongmu.domain.main.dto.response.ScheduleDto(" +
//           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, s.place, " +
//           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
//           "(SELECT COUNT(a) FROM Attendee a WHERE a.schedule = s)) " +
//           "FROM Schedule s " +
//           "WHERE s.type = 'PERSONAL'")
//    List<ScheduleDto> findPersonalSchedules();
//
//    @Query("SELECT DISTINCT DAY(s.scheduleStartTime) FROM Schedule s " +
//           "WHERE YEAR(s.scheduleStartTime) = :year " +
//           "AND MONTH(s.scheduleStartTime) = :month")
//    List<Integer> findScheduleDatesForMonth(@Param("year") int year, @Param("month") int month);
//
//    @Query("SELECT new com.b110.jjeonchongmu.domain.main.dto.response.ScheduleDto(" +
//           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, s.place, " +
//           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
//           "(SELECT COUNT(a) FROM Attendee a WHERE a.schedule = s)) " +
//           "FROM Schedule s " +
//           "WHERE DATE(s.scheduleStartTime) = :date")
//    List<ScheduleDto> findSchedulesByDate(@Param("date") LocalDate date);
//
//    @Query("SELECT new com.b110.jjeonchongmu.domain.main.dto.response.ScheduleDto(" +
//           "s.gathering.id, s.gathering.name, s.id, s.title, s.detail, s.place, " +
//           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
//           "(SELECT COUNT(a) FROM Attendee a WHERE a.schedule = s)) " +
//           "FROM Schedule s " +
//           "WHERE s.type = 'GATHERING'")
//    List<ScheduleDto> findGatheringSchedules();
} 