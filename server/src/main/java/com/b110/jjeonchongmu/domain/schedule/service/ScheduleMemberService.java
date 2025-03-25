package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleMemberRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleMemberService {

    private final ScheduleMemberRepo scheduleMemberRepo;

    // 일정 참석
    @Transactional
    public void attendSchedule(Long userId,Long scheduleId) {
//        scheduleMemberRepo.insertScheduleMember(userId,scheduleId);
    }
    // 일정 참석 취소
    @Transactional
    public void cancelAttendance(Long userId,Long scheduleId) {
//        scheduleMemberRepo.deleteScheduleMember(userId,scheduleId);
    }
}
