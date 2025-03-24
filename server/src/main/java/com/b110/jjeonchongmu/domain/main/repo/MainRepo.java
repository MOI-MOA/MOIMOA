package com.b110.jjeonchongmu.domain.main.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MainRepo extends JpaRepository<Schedule, Long> {

    /**
     * 미확인 일정 수 조회
     */
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.checked = false")
    int countUncheckSchedules();

    /**
     * 오늘의 일정 조회
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE DATE(s.startDate) = CURRENT_DATE " +
           "ORDER BY s.startDate")
    List<Schedule> findTodaySchedules();

    /**
     * 다가오는 일정 조회
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE s.startDate > CURRENT_DATE " +
           "ORDER BY s.startDate")
    List<Schedule> findUpcomingSchedules();

    /**
     * 미확인 일정 조회
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE s.checked = false " +
           "ORDER BY s.startDate")
    List<Schedule> findUncheckSchedules();

    /**
     * 개인 일정 조회
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE s.type = 'PERSONAL' " +
           "ORDER BY s.startDate")
    List<Schedule> findPersonalSchedules();

    /**
     * 특정 연월의 일정 조회
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE YEAR(s.startDate) = :year " +
           "AND MONTH(s.startDate) = :month " +
           "ORDER BY s.startDate")
    List<Schedule> findSchedulesByYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * 특정 날짜의 일정 조회
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE DATE(s.startDate) = :date " +
           "ORDER BY s.startDate")
    List<Schedule> findSchedulesByDate(@Param("date") LocalDate date);
} 