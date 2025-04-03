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
    public List<ScheduleMemberDTO> getScheduleMember(Long userId, Long scheduleId){

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Schedule Not Found"));
        Long gatheringId = schedule.getGathering().getGatheringId();

        boolean isMember = scheduleMemberRepo.existsByUserIdAndGatheringId(userId, gatheringId);
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

        schedule.updateScheduleAttendees(scheduleMember);
        scheduleMemberRepo.save(scheduleMember);

        GatheringMember gatheringMember = gatheringMemberRepo.findGatheringMemberByScheduleIdAndUserId(scheduleId,userId);

        // 개인돈이 인당예산보다 작으면 오류 발생
        if(gatheringMember.getGatheringMemberAccountBalance() < schedule.getPerBudget()){
            Long gatheringMemberBalance = gatheringMember.getGatheringMemberAccountBalance(); // 잔액
            Long remainingAmount = schedule.getPerBudget() - gatheringMemberBalance;
            gatheringMember.decreaseGatheringMemberAccountBalance(gatheringMemberBalance);

            if(remainingAmount>gatheringMember.getGatheringMemberAccountDeposit()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"보증금 까지 했는데 돈부족해");
            }
            gatheringMember.decreaseGatheringMemberAccountDeposit(remainingAmount);
        } else {
        // 개인돈에서 인당예산 만큼 차감
        gatheringMember.decreaseGatheringMemberAccountBalance(schedule.getPerBudget());
        // 일정계좌 잔액 인당예산 만큼 증가
        schedule.getScheduleAccount().increaseBalance(schedule.getPerBudget());
        }

    }
    // 일정 참석 취소
    @Transactional
    public void cancelAttendance(Long userId, Long scheduleId) {
        boolean isMember = scheduleMemberRepo.existsByUserIdAndScheduleId(userId, scheduleId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        GatheringMember gatheringMember = gatheringMemberRepo.findGatheringMemberByScheduleIdAndUserId(scheduleId,userId);

        ScheduleMember scheduleMember = scheduleMemberRepo.findByScheduleIdAndScheduleMemberUserId(scheduleId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule member not found"));
        Gathering gathering = schedule.getGathering();


        if(LocalDateTime.now().isBefore(schedule.getPenaltyApplyDate())){
            Long gatheringMemberDeposit = gatheringMember.getGatheringMemberAccountDeposit();
            Long haveTotransferDeposit = gathering.getGatheringDeposit() - gatheringMemberDeposit;
            // 개인 보증금이 모임의 정해진 보증금 금액보다 적으면 개인 보증금부터 채워
            if(gatheringMemberDeposit < gathering.getGatheringDeposit()){
                gatheringMember.increaseGatheringMemberAccountDeposit(haveTotransferDeposit);
                Long remainingAmount = schedule.getPerBudget() - haveTotransferDeposit;
                gatheringMember.increaseGatheringMemberAccountDeposit(remainingAmount);
            } else {
                gatheringMember.increaseGatheringMemberAccountBalance(schedule.getPerBudget());
            }

                schedule.getScheduleAccount().decreaseBalance(schedule.getPerBudget());
                scheduleMemberRepo.delete(scheduleMember);

        } else {
            scheduleMember.updateIsPenaltyApplyToTrue();
        }
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
