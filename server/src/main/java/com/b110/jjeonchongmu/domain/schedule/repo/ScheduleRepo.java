package com.b110.jjeonchongmu.domain.schedule.repo;

import com.b110.jjeonchongmu.domain.mypage.dto.statistics.GroupExpenseData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.MonthlyExpenseData;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleCreateDTO;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleMemberDTO;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleUpdateDTO;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDTO;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    @Query("SELECT new com.b110.jjeonchongmu.domain.mypage.dto.statistics.MonthlyExpenseData(CONCAT(YEAR(s.startTime), '년 ', MONTH(s.startTime), '월'), SUM(s.perBudget)) " +
            "FROM Schedule s " +
            "WHERE s.subManager.userId = :userId AND s.startTime BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(s.startTime), MONTH(s.startTime) " +
            "ORDER BY YEAR(s.startTime), MONTH(s.startTime)")
    List<MonthlyExpenseData> findMonthlyExpenseDataByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.b110.jjeonchongmu.domain.mypage.dto.statistics.GroupExpenseData(s.gathering.gatheringName, SUM(s.perBudget)) " +
            "FROM Schedule s " +
            "WHERE s.subManager.userId = :userId " +
            "GROUP BY s.gathering.gatheringName " +
            "ORDER BY SUM(s.perBudget) DESC")

    List<GroupExpenseData> findGroupExpensesByUserId(@Param("userId") Long userId);

    // 일정 목록 조회
    List<Schedule> findByGatheringGatheringId(Long gatheringId);

    // 일정 상세 조회
    Optional<Schedule> findById(Long scheduleId);



    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE s.subManager.userId = :userId AND s.id = :scheduleId")
    boolean checkExistsByUserIdAndScheduleId(@Param("userId") Long userId, @Param("scheduleId") Long scheduleId);

}



