package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleMemberRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleMemberService {
    
    private final ScheduleMemberRepo scheduleMemberRepo;

    @Transactional
    public void addMember(Schedule schedule, User user) {
        if (scheduleMemberRepo.existsByScheduleAndScheduleMember(schedule, user)) {
            throw new CustomException(ErrorCode.ALREADY_SCHEDULE_MEMBER);
        }

        ScheduleMember scheduleMember = ScheduleMember.builder()
                .schedule(schedule)
                .user(user)
                .scheduleIsCheck(false)
                .build();

        scheduleMemberRepo.save(scheduleMember);
    }

    @Transactional
    public void removeMember(Schedule schedule, User user) {
        ScheduleMember scheduleMember = scheduleMemberRepo.findByScheduleAndScheduleMember(schedule, user)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_MEMBER_NOT_FOUND));

        scheduleMemberRepo.delete(scheduleMember);
    }

    @Transactional
    public void checkAttendance(Schedule schedule, User user) {
        ScheduleMember scheduleMember = scheduleMemberRepo.findByScheduleAndScheduleMember(schedule, user)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_MEMBER_NOT_FOUND));

        scheduleMember.checkAttendance();
    }
}
