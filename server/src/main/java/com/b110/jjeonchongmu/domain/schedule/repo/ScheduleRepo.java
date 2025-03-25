package com.b110.jjeonchongmu.domain.schedule.repo;

import com.b110.jjeonchongmu.domain.mypage.dto.statistics.GroupExpenseData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.MonthlyExpenseData;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleCreateDTO;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleMemberDTO;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleUpdateDTO;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDTO;
import org.springframework.data.repository.query.Param;

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



//    @Query("SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDTO(" +
//           "s.gathering.gatheringId, s.gathering.gatheringName, s.id, s.title, s.detail, s.place, " +
//           "s.startTime, s.perBudget, s.totalBudget, s.penaltyApplyDate, s.status, " +
//           "SIZE(s.attendees)) " +
//           "FROM Schedule s")
//    List<ScheduleListDTO> findAllSchedules();
    
//    @Query("SELECT new com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDetailDTO(" +
//           "s.title, s.detail, s.place, s.startTime, s.perBudget, s.totalBudget, " +
//           "s.penaltyApplyDate, s.gathering.penaltyRate, s.id, s.gathering.gatheringId, " +
//           "s.manager.userId, s.gathering.gatheringName, SIZE(s.attendees), " +
//           "EXISTS (SELECT 1 FROM ScheduleMember sm WHERE sm.schedule = s AND sm.scheduleMember.userId = :userId)) " +
//           "FROM Schedule s WHERE s.id = :scheduleId")
//    Optional<ScheduleDetailDTO> findScheduleDetailById(@Param("scheduleId") Long scheduleId, @Param("userId") Long userId);

    // 모임 일정목록 조회
    // gathering_id로 Schedule 엔티티 목록 조회
    List<Schedule> findByGatheringGatheringId(Long gatheringId);

    
    // dto를 반환받으려면 @Query로 정의해야함 -> 이방법이 복잡해서 entity로 리턴받고 service에서 entity -> dto로 수정
    // 일정 상세조회
//    ScheduleDetailDTO findScheduleDetailById(Long scheduleId);

    // 일정 멤버(참여자) 목록 조회
//    List<ScheduleMemberDTO> findAllScheduleMembers(Long scheduleId);

    // 일정 생성(총무만)
//    void insertSchedule(Long gatheringId, ScheduleCreateDTO scheduleCreateDTO);
//
//    // 일정 수정(총무만)
//    void updateSchedule(Long scheduleId, ScheduleUpdateDTO scheduleUpdateDTO);
    
    // delete는 jpa에서 정의해놓은 메서드 사용하자
    // 연관관계 매핑이 많은 경우 jdbc로 최적화해야함
    // 일정 삭제(총무만)
//    void deleteSchedule(Long scheduleId);

    // 일정별 참석자 수 조회
    @Query("SELECT COUNT(sm) FROM ScheduleMember sm WHERE sm.schedule.id = :scheduleId")
    long countAttendeesByScheduleId(@Param("scheduleId") Long scheduleId);



}



