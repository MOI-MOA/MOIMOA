package com.b110.jjeonchongmu.domain.schedule.repo;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleMemberRepo extends JpaRepository<ScheduleMember, Long> {
    // 일정 멤버(참여자) 목록 조회
    List<ScheduleMember> findByScheduleId(Long scheduleId);

    // 일정 id 와 일정멤버 id로 scheduleMember 데이터 조회
    Optional<ScheduleMember> findByScheduleIdAndScheduleMemberUserId(Long scheduleId, Long userId);
}
