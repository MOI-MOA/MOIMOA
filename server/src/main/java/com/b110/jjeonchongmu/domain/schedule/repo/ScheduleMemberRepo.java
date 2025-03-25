package com.b110.jjeonchongmu.domain.schedule.repo;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleMemberRepo extends JpaRepository<ScheduleMember, Long> {


    // 일정 참석
//    void insertScheduleMember(Long userId, Long scheduleId);
//
//    // 일정 참석 취소
//    void deleteScheduleMember(Long userId, Long scheduleId);


}
