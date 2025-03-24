package com.b110.jjeonchongmu.domain.schedule.repo;

import com.b110.jjeonchongmu.domain.schedule.dto.*;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

    // 모임 일정목록 조회
    // gathering_id로 Schedule 엔티티 목록 조회
    List<Schedule> findByGatheringGatheringId(Long gatheringId);


    // 일정 상세조회
    ScheduleDetailDTO findScheduleDetailById(Long scheduleId);

    // 일정 멤버(참여자) 목록 조회
    List<ScheduleMemberDTO> selectAllScheduleMemebers(Long scheduleId);

    // 일정 생성(총무만)
    void insertSchedule(Long gatheringId, ScheduleCreateDTO scheduleCreateDTO);

    // 일정 수정(총무만)
    void updateSchedule(Long scheduleId, ScheduleUpdateDTO scheduleUpdateDTO);

    // 일정 삭제(총무만)
    void deleteSchedule(Long scheduleId);

    // 일정별 참석자 수 조회
    @Query("SELECT COUNT(sm) FROM ScheduleMember sm WHERE sm.schedule.id = :scheduleId")
    long countAttendeesByScheduleId(@Param("scheduleId") Long scheduleId);
}
