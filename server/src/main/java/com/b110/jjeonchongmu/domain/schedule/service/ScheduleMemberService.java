package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
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

import java.time.LocalDateTime;
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
    @Transactional
    public List<ScheduleMemberDTO> getScheduleMember(Long userId, Long scheduleId){

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Schedule Not Found"));
        Long gatheringId = schedule.getGathering().getGatheringId();

        boolean isMember = gatheringMemberRepo.existsByUserIdAndGatheringId(userId, gatheringId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        return scheduleMemberRepo.findByScheduleIdAndIsAttendTrue(scheduleId)
                .stream()
                .map(ScheduleMemberDTO::from)
                .collect(Collectors.toList());
    };

    // 일정 참석
    @Transactional
    public void attendSchedule(Long userId,Long scheduleId) {

        // 스케줄 찾고
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        // 유저 찾고
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // 일정 참석할때 돈 확인하는 로직
        Long gatheringId = schedule.getGathering().getGatheringId();
        GatheringMember gatheringMember = gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(gatheringId, userId)
                .orElseThrow(() -> new RuntimeException("userId와 gatheringId로 gatheringMember를 찾을 수 없습니다."));
        if (gatheringMember.getGatheringMemberAccountBalance() < schedule.getPerBudget()) {
            throw new RuntimeException("돈이 부족해 참석할 수 없습니다.");
        }
        // 참석 true로 바꿔줌.
        ScheduleMember scheduleMember = scheduleMemberRepo.getScheduleMemberByUserIdAndScheduleId(userId, scheduleId)
                .orElseThrow(() -> new RuntimeException("userId와 scheduleId로 scheduleMember를 찾을 수 없습니다."));
        scheduleMember.updateScheduleIsCheckToTrue();
        scheduleMember.updateIsAttenedToTrue();
        // 개인돈에서 인당예산 만큼 차감
        gatheringMember.decreaseGatheringMemberAccountBalance(schedule.getPerBudget());
        // 일정계좌 잔액 인당예산 만큼 증가
        schedule.getScheduleAccount().increaseBalance(schedule.getPerBudget());

    }

    @Transactional
    // 일정 참여 거절
    public void attendRejectSchedule(Long userId, Long scheduleId) {

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        //모임멤버인지 확인하는 로직
        boolean isMember = gatheringMemberRepo.existsByUserIdAndGatheringId(userId, schedule.getGathering().getGatheringId());
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this gathering");
        }

        ScheduleMember scheduleMember = scheduleMemberRepo.findByScheduleIdAndScheduleMemberUserIdAndIsAttendFalse(scheduleId,userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"ScheduleMember Not Found"));

        scheduleMember.updateScheduleIsCheckToTrue();
    }

    // 일정 참석 취소
    @Transactional
    public void cancelAttendance(Long userId, Long scheduleId) {
        boolean isMember = scheduleMemberRepo.existsByUserIdAndScheduleIdIsAttendTrue(userId, scheduleId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        GatheringMember gatheringMember = gatheringMemberRepo.findGatheringMemberByScheduleIdAndUserId(scheduleId,userId);

        ScheduleMember scheduleMember = scheduleMemberRepo.findByScheduleIdAndScheduleMemberUserIdAndIsAttendTrue(scheduleId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule member not found"));



        if(LocalDateTime.now().isBefore(schedule.getPenaltyApplyDate())){
            gatheringMember.increaseGatheringMemberAccountBalance(schedule.getPerBudget());
            schedule.getScheduleAccount().decreaseBalance(schedule.getPerBudget());
            scheduleMember.updateIsAttenedToFalse();

        } else {
            Long paybackAmount = schedule.getPerBudget() * schedule.getPenaltyRate();

            schedule.getScheduleAccount().decreaseBalance(paybackAmount);
            gatheringMember.increaseGatheringMemberAccountBalance(paybackAmount);

            scheduleMember.updateIsPenaltyApplyToTrue();
            scheduleMember.updateIsAttenedToFalse();
        }
    }

    @Transactional
    // 일정을 만들때 부총무 세팅
    public void setSubManager(Long gatheringId, Long scheduleId, Long subManagerId, Long perBudget){

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        System.out.println("부총무 아이디 : " + subManagerId);
        User subManager = userRepo.findByUserId(subManagerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubManger not found"));
        GatheringMember gatheringMember= gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(gatheringId,subManagerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"GatheringMember Not Found"));

        List<GatheringMember> gatheringMembers = gatheringMemberRepo.findByGatheringGatheringId(gatheringId);

        for(GatheringMember g : gatheringMembers){
            ScheduleMember scheduleMember = ScheduleMember.builder()
                .schedule(schedule)
                .scheduleMember(g.getGatheringMemberUser())
                .scheduleIsCheck(false)
                .isPenaltyApply(false)
                .isAttend(false)
                .build();
            scheduleMemberRepo.save(scheduleMember);
        }

        ScheduleMember subManagerScheduleMember = scheduleMemberRepo.findByScheduleIdAndScheduleMemberUserIdAndIsAttendFalse(scheduleId,subManagerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"scheduleMember Not Found"));

        subManagerScheduleMember.updateIsAttenedToTrue();
        subManagerScheduleMember.updateScheduleIsCheckToTrue();

        gatheringMember.decreaseGatheringMemberAccountBalance(perBudget);
    }

}
