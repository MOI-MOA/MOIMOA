package com.b110.jjeonchongmu.domain.schedule.repo;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleMemberRepo extends JpaRepository<ScheduleMember, Long> {
    Optional<ScheduleMember> findByScheduleAndScheduleMember(Schedule schedule, User user);
    boolean existsByScheduleAndScheduleMember(Schedule schedule, User user);
    List<ScheduleMember> findAllBySchedule(Schedule schedule);
    void deleteAllBySchedule(Schedule schedule);
}
