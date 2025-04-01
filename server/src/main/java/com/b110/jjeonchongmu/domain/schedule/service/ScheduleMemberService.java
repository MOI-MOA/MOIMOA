package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringMemberRepo;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleMemberDTO;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleMemberRepo;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleMemberService {

    private final ScheduleMemberRepo scheduleMemberRepo;
    private final UserRepo userRepo;
    private final ScheduleRepo scheduleRepo;
    private final GatheringMemberRepo gatheringMemberRepo;
    // 일정 멤버(참여자) 목록 조회
    public List<ScheduleMemberDTO> getScheduleMember(Long userId, Long scheduleId){
        boolean isMember = scheduleMemberRepo.existsByUserIdAndGatheringId(userId, scheduleId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        return scheduleMemberRepo.findByScheduleId(scheduleId)
                .stream()
                .map(ScheduleMemberDTO::from)
                .collect(Collectors.toList());
    };

    // 일정 참석
    @Transactional
    public void attendSchedule(Long userId,Long scheduleId) {

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Long gatheringId = schedule.getGathering().getGatheringId(); // schedule 로 gatheringId 조회
        boolean isMember = gatheringMemberRepo.existsByUserIdAndGatheringId(userId, gatheringId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this gathering");
        }

        ScheduleMember scheduleMember = ScheduleMember.builder()
                .schedule(schedule)
                .scheduleMember(user)
                .build();

        scheduleMemberRepo.save(scheduleMember);
    }
    // 일정 참석 취소
    @Transactional
    public void cancelAttendance(Long userId, Long scheduleId) {
        boolean isMember = scheduleMemberRepo.existsByUserIdAndGatheringId(userId, scheduleId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        ScheduleMember scheduleMember = scheduleMemberRepo.findByScheduleIdAndScheduleMemberUserId(scheduleId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule member not found"));
        
        scheduleMemberRepo.delete(scheduleMember);
    }

    // 일정을 만들때 부총무 세팅
    public void setSubManager(Long gatheringId, Long scheduleId, Long subManagerId, Long perBudget){

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        System.out.println("부총무 아이디 : " + subManagerId);
        User subManager = userRepo.findByUserId(subManagerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubManger not found"));
        GatheringMember gatheringMember= gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(gatheringId,subManagerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"GatheringMember Not Found"));

        ScheduleMember scheduleMember = ScheduleMember.builder()
                .schedule(schedule)
                .scheduleMember(subManager)
                .build();
        gatheringMember.decreaseGatheringMemberAccountBalance(perBudget);


        scheduleMemberRepo.save(scheduleMember);
    }




}
