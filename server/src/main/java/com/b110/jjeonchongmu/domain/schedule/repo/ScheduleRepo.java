package com.b110.jjeonchongmu.domain.schedule.repo;

import com.b110.jjeonchongmu.domain.mypage.dto.statistics.GroupExpenseData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.MonthlyExpenseData;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDTO;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDTO;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    @Query("SELECT new com.b110.jjeonchongmu.domain.mypage.dto.statistics.MonthlyExpenseData(CONCAT(YEAR(s.startTime), '년 ', MONTH(s.startTime), '월'), SUM(s.perBudget)) " +
            "FROM Schedule s " +
            "WHERE s.manager.userId = :userId AND s.startTime BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(s.startTime), MONTH(s.startTime) " +
            "ORDER BY YEAR(s.startTime), MONTH(s.startTime)")
    List<MonthlyExpenseData> findMonthlyExpenseDataByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.b110.jjeonchongmu.domain.mypage.dto.statistics.GroupExpenseData(s.gathering.gatheringName, SUM(s.perBudget)) " +
            "FROM Schedule s " +
            "WHERE s.manager.userId = :userId " +
            "GROUP BY s.gathering.gatheringName " +
            "ORDER BY SUM(s.perBudget) DESC")
    List<GroupExpenseData> findGroupExpensesByUserId(@Param("userId") Long userId);

    

    @Query("SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDTO(" +
           "s.gathering.gatheringId, s.gathering.gatheringName, s.id, s.title, s.detail, s.place, " +
           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
           "SIZE(s.attendees)) " +
           "FROM Schedule s")
    List<ScheduleListDTO> findAllSchedules();

    @Query("SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDTO(" +
           "s.title, s.detail, s.place, s.startTime, s.perBudget, s.totalBudget, " +
           "s.penaltyApplyDate, s.gathering.penaltyRate, s.id, s.gathering.gatheringId, " +
           "s.manager.userId, s.gathering.gatheringName, SIZE(s.attendees), " +
           "EXISTS (SELECT 1 FROM ScheduleMember sm WHERE sm.schedule = s AND sm.scheduleMember.userId = :userId)) " +
           "FROM Schedule s WHERE s.id = :scheduleId")
    Optional<ScheduleDetailDTO> findScheduleDetailById(@Param("scheduleId") Long scheduleId, @Param("userId") Long userId);
}
