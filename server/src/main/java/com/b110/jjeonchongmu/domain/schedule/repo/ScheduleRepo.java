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
    @Query("SELECT new com.b110.jjeonchongmu.domain.mypage.dto.statistics.MonthlyExpenseData(" +
            "CONCAT(YEAR(s.startTime), '년 ', MONTH(s.startTime), '월'), SUM(s.perBudget)) " +
            "FROM Schedule s " +
            "WHERE s.subManager.userId = :userId AND s.startTime BETWEEN :startDate AND :endDate " +
            "GROUP BY CONCAT(YEAR(s.startTime), '년 ', MONTH(s.startTime), '월') " +
            "ORDER BY MIN(s.startTime)")  // ORDER BY도 명확하게 표현!
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

    // 일정 생성(총무만)
//    void insertSchedule(Long gatheringId, ScheduleCreateDTO scheduleCreateDTO);
//
//    // 일정 수정(총무만)
//    void updateSchedule(Long scheduleId, ScheduleUpdateDTO scheduleUpdateDTO);
    
    // delete는 jpa에서 정의해놓은 메서드 사용하자
    // 연관관계 매핑이 많은 경우 jdbc로 최적화해야함
    // 일정 삭제(총무만)
//    void deleteSchedule(Long scheduleId);

}



