package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.account.repo.ScheduleAccountRepo;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringMemberRepo;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.schedule.dto.*;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleMemberRepo;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;

@Service
@RequiredArgsConstructor
//@Transactional
public class ScheduleService {

    private final ScheduleRepo scheduleRepo;
    private final GatheringRepo gatheringRepo;
    private final UserRepo userRepo;
    private final ScheduleMemberRepo scheduleMemberRepo;
    private final GatheringMemberRepo gatheringMemberRepo;
    private final ScheduleAccountRepo scheduleAccountRepo;

    // 모임 일정목록 조회
    public List<ScheduleDTO> getScheduleList(Long userId, Long gatheringId) {
        boolean isMember = gatheringMemberRepo.existsByUserIdAndGatheringId(userId, gatheringId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this gathering");
        }

        return scheduleRepo.findByGatheringGatheringId(gatheringId)
                .stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());
    }

    // 일정 상세조회
    public ScheduleDetailDTO getScheduleDetail(Long userId, Long scheduleId) {

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        Long gatheringId = schedule.getGathering().getGatheringId();

        boolean isMember = scheduleMemberRepo.existsByUserIdAndGatheringId(userId, gatheringId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        return ScheduleDetailDTO.from(schedule);
    }

    // 일정 생성(총무만)
    public Long createSchedule(Long userId, Long gatheringId, ScheduleCreateDTO scheduleCreateDTO) {
        boolean isManager = (gatheringRepo.countByUserIdAndGatheringId(userId, gatheringId)) > 0;
        if (!isManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        Gathering gathering = gatheringRepo.findById(gatheringId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gathering not found"));

        System.out.println("11111 부총무 아이디 : " + scheduleCreateDTO.getSubManagerId());
        User subManager = userRepo.findByUserId(scheduleCreateDTO.getSubManagerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubManger not found"));

//        일정 생성후에 계좌를 생성해야하고  부총무가 일정멤버로 지정되어야 함
        Schedule schedule = Schedule.builder()
                .gathering(gathering)
                .subManager(subManager)
                .title(scheduleCreateDTO.getScheduleTitle())
                .detail(scheduleCreateDTO.getScheduleDetail())
                .place(scheduleCreateDTO.getSchedulePlace())
                .startTime(scheduleCreateDTO.getScheduleStartTime())
                .perBudget(scheduleCreateDTO.getPerBudget())
                .penaltyApplyDate(scheduleCreateDTO.getPenaltyApplyDate())
                .penaltyRate(scheduleCreateDTO.getPenaltyRate())
                .status(0)
                .build();

        Schedule savedSchedule = scheduleRepo.save(schedule);

        // 부총무 일정멤버에 추가
        List<ScheduleMember> attendees = new ArrayList<>();
        ScheduleMember attendee = ScheduleMember.builder()
                .schedule(savedSchedule)
                .scheduleMember(subManager)
                .build();
        attendees.add(attendee);

        scheduleRepo.save(savedSchedule);

        return savedSchedule.getId();
    }

    // 일정 수정(부총무만)
    public void updateSchedule(Long userId, Long scheduleId, ScheduleUpdateDTO scheduleUpdateDTO) {
        boolean isSubManager = scheduleRepo.checkExistsByUserIdAndScheduleId(userId, scheduleId);
        if (!isSubManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }


        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found"));

        schedule.updateTitle(scheduleUpdateDTO.getScheduleTitle());
        schedule.updateDetail(scheduleUpdateDTO.getScheduleDetail());
        schedule.updatePlace(scheduleUpdateDTO.getSchedulePlace());
        schedule.updatePerBudget(scheduleUpdateDTO.getPerBudget());

    }

    // 일정 삭제(부총무만)
    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        System.out.println("일정 삭제 처음부분");
        scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        boolean isSubManager = scheduleRepo.checkExistsByUserIdAndScheduleId(userId, scheduleId);
        if (!isSubManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule Not Found"));
        Long scheduleAccountId = schedule.getScheduleAccount().getAccountId();
        ScheduleAccount scheduleAccount = scheduleAccountRepo.findByAccount(scheduleAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ScehduleAccount Not Found"));

        /// 계좌 잔액 이동을위한 변수들
        Integer penaltyApplyCount = scheduleMemberRepo.countByScheduleIdAndPenaltyApplied(scheduleId);
        Integer attendeeCount = scheduleMemberRepo.countByScheduleIdAndPenaltyNotApplied(scheduleId);
        Integer penaltyRate = schedule.getPenaltyRate();
        Long remainingAmount = scheduleAccount.getAccountBalance();
        Long perBudget = schedule.getPerBudget();
        Long AmountToBeGuaranteed = (perBudget * penaltyRate) / 100;
        System.out.println("페이백 대상자 수 : " + penaltyApplyCount);
        System.out.println("참여자 수 : " + attendeeCount);
        System.out.println("인당 예산 : " + perBudget);
        System.out.println("페이백 대상자에게 보상해줘야할 금액 : " + perBudget);

        List<GatheringMember> penaltyAppliedMembers = gatheringMemberRepo.findGatheringMembersByScheduleIdAndPenaltyApplied(scheduleId);
        List<GatheringMember> attendeeMembers = gatheringMemberRepo.findGatheringMembersByScheduleIdAndPenaltyNotApplied(scheduleId);
        System.out.println("참여자 GatheringMember 목록 : " + attendeeMembers);
        //////

        // 일정 멤버 삭제
        scheduleMemberRepo.deleteAllByScheduleId(scheduleId);
        System.out.println("일정 멤버 삭제");
        // 일정 삭제
        scheduleRepo.deleteById(scheduleId);

        System.out.println("일정 삭제");
        // 일정 계좌 삭제


        System.out.println("일정 아이디 : " + scheduleId);


        if (AmountToBeGuaranteed * penaltyApplyCount <= remainingAmount) {

            penaltyAppliedMembers.forEach(member -> member.increaseGatheringMemberAccountBalance(AmountToBeGuaranteed));

            // 페이백 대상자들에게 페이백 비용 나눠주기
            remainingAmount -= AmountToBeGuaranteed * penaltyApplyCount;

            Long attendeeMembersAmount = remainingAmount / attendeeCount; // 참여자가 한명당 받아야하는 금액

            attendeeMembers.forEach(member -> member.increaseGatheringMemberAccountBalance(attendeeMembersAmount));

            remainingAmount -= attendeeMembersAmount * attendeeCount;
            // 나머지 1원들
            Iterator<GatheringMember> iterator = attendeeMembers.iterator();
            while (remainingAmount > 0 && iterator.hasNext()) {
                iterator.next().increaseGatheringMemberAccountBalance(1L);
                remainingAmount--;
            }
        }
        else{
            // 페이백 대상자 한명에게 나눠줘야할 금액
            Long penaltyAppliedMembersAmount  = remainingAmount / penaltyApplyCount;

            penaltyAppliedMembers.forEach(member -> member.increaseGatheringMemberAccountBalance(penaltyAppliedMembersAmount));

            // 나눠준만큼 금액 차감
            remainingAmount -= penaltyAppliedMembersAmount * penaltyApplyCount;

            Iterator<GatheringMember> iterator = penaltyAppliedMembers.iterator();
            while (remainingAmount > 0 && iterator.hasNext()) {
                iterator.next().increaseGatheringMemberAccountBalance(1L);
                remainingAmount--;
            }
        }

        // 일정계좌에 남은 금액을 0으로 만듬 (사실 필요없는데 일단 넣어놈)
        scheduleAccount.decreaseBalance(scheduleAccount.getAccountBalance());
        ///////////////////////////////////////////////////////

        System.out.println("일정 계좌 돈 이동 완료 ");
            scheduleAccountRepo.deleteById(scheduleAccountId);
        System.out.println("일정 계좌 삭제완료 ");


        }
    }
