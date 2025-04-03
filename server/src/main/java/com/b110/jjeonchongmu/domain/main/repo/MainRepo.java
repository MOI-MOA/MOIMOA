package com.b110.jjeonchongmu.domain.main.repo;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MainRepo extends JpaRepository<Schedule, Long> {

    /**
     * 미확인 일정 개수 조회
     */
    @Query(value = "SELECT COUNT(*) FROM schedule s " +
            "JOIN schedule_member sm ON s.schedule_id = sm.schedule_id " +
            "WHERE sm.user_id = :userId AND sm.schedule_is_check = false", nativeQuery = true)
    int countUncheckSchedules(@Param("userId") Long userId);

    /**
     * 미확인 일정 목록 조회
     */
    @Query(value = "SELECT s.schedule_id, s.schedule_title, s.schedule_detail, s.schedule_place, s.schedule_start_time, " +
            "g.gathering_id, g.gathering_name, COUNT(sm.schedule_member_id) " +
            "FROM schedule s " +
            "LEFT JOIN gathering g ON s.gathering_id = g.gathering_id " +
            "JOIN schedule_member sm ON s.schedule_id = sm.schedule_id " +
            "WHERE sm.user_id = :userId AND sm.schedule_is_check = false " +
            "GROUP BY s.schedule_id, g.gathering_id " +
            "ORDER BY s.schedule_start_time", nativeQuery = true)
    List<Object[]> findUncheckSchedules(@Param("userId") Long userId);

    /**
     * 개인 일정 목록 조회
     */
    @Query(value = "SELECT s.schedule_id, s.schedule_title, s.schedule_detail, s.schedule_place, s.schedule_start_time, " +
            "g.gathering_id, g.gathering_name, COUNT(sm.schedule_member_id) " +
            "FROM schedule s " +
            "LEFT JOIN gathering g ON s.gathering_id = g.gathering_id " +
            "JOIN schedule_member sm ON s.schedule_id = sm.schedule_id " +
            "WHERE sm.user_id = :userId AND s.gathering_id IS NULL " +
            "GROUP BY s.schedule_id, g.gathering_id " +
            "ORDER BY s.schedule_start_time", nativeQuery = true)
    List<Object[]> findPersonalSchedules(@Param("userId") Long userId);

    /**
     * 해당 월에 일정이 있는 날짜와 일정 수 조회
     */
    @Query(value = "SELECT DAY(s.schedule_start_time) as day, COUNT(DISTINCT s.schedule_id) as schedule_count " +
            "FROM schedule s " +
            "JOIN schedule_member sm ON s.schedule_id = sm.schedule_id " +
            "WHERE sm.user_id = :userId " +
            "AND YEAR(s.schedule_start_time) = :year AND MONTH(s.schedule_start_time) = :month " +
            "GROUP BY DAY(s.schedule_start_time) " +
            "ORDER BY DAY(s.schedule_start_time)", nativeQuery = true)
    List<Object[]> findMonthSchedules(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    /**
     * 특정 날짜의 일정 조회
     */
    @Query(value = "SELECT s.schedule_id, s.schedule_title, s.schedule_detail, s.schedule_place, s.schedule_start_time, " +
            "g.gathering_id, g.gathering_name, COUNT(sm.schedule_member_id) " +
            "FROM schedule s " +
            "LEFT JOIN gathering g ON s.gathering_id = g.gathering_id " +
            "JOIN schedule_member sm ON s.schedule_id = sm.schedule_id " +
            "WHERE sm.user_id = :userId " +
            "AND DATE(s.schedule_start_time) = :targetDate " +
            "GROUP BY s.schedule_id, g.gathering_id " +
            "ORDER BY s.schedule_start_time", nativeQuery = true)
    List<Object[]> findDaySchedules(@Param("userId") Long userId, @Param("targetDate") LocalDate targetDate);

    /**
     * 오늘 일정 조회
     */
    @Query(value = "SELECT s.schedule_id, s.schedule_title, s.schedule_detail, s.schedule_place, s.schedule_start_time, " +
            "g.gathering_id, g.gathering_name, COUNT(sm.schedule_member_id) " +
            "FROM schedule s " +
            "LEFT JOIN gathering g ON s.gathering_id = g.gathering_id " +
            "JOIN schedule_member sm ON s.schedule_id = sm.schedule_id " +
            "WHERE sm.user_id = :userId " +
            "AND DATE(s.schedule_start_time) = CURRENT_DATE " +
            "GROUP BY s.schedule_id, g.gathering_id " +
            "ORDER BY s.schedule_start_time", nativeQuery = true)
    List<Object[]> findTodaySchedules(@Param("userId") Long userId);

    /**
     * 다가오는 일정 조회 (오늘 이후의 일정 중 가장 가까운 5개)
     */
    @Query(value = "SELECT s.schedule_id, s.schedule_title, s.schedule_detail, s.schedule_place, s.schedule_start_time, " +
            "g.gathering_id, g.gathering_name, COUNT(sm.schedule_member_id) " +
            "FROM schedule s " +
            "LEFT JOIN gathering g ON s.gathering_id = g.gathering_id " +
            "JOIN schedule_member sm ON s.schedule_id = sm.schedule_id " +
            "WHERE sm.user_id = :userId " +
            "AND s.schedule_start_time >= CURRENT_TIMESTAMP " +
            "AND sm.schedule_is_check = true " +
            "GROUP BY s.schedule_id, g.gathering_id " +
            "ORDER BY s.schedule_start_time " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> findUpcomingSchedules(@Param("userId") Long userId);
}