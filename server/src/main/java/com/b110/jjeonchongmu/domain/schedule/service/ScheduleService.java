package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.account.dto.MakeAccountDTO;
import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.account.repo.ScheduleAccountRepo;
import com.b110.jjeonchongmu.domain.account.service.ScheduleAccountService;
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

import static com.b110.jjeonchongmu.domain.gathering.entity.QGathering.gathering;

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
    private final ScheduleAccountService scheduleAccountService;
    private final ScheduleMemberService scheduleMemberService;

    // 모임 일정목록 조회
    @Transactional
    public List<ScheduleDTO> getScheduleList(Long userId, Long gatheringId) {
        boolean isMember = gatheringMemberRepo.existsByUserIdAndGatheringId(userId, gatheringId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this gathering");
        }
        User currentUser = userRepo.getUserByUserId(userId);

        return scheduleRepo.findByGatheringGatheringId(gatheringId)
                .stream()
                .map(ScheduleDTO::from)
                .peek(dto -> dto.updateIsSubManager(
                        scheduleRepo.findById(dto.getScheduleId())
                                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Schedule Not Found"))
                                .getSubManager().equals(currentUser)
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    // 일정 상세조회
    public ScheduleDetailDTO getScheduleDetail(Long userId, Long scheduleId) {

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        Long gatheringId = schedule.getGathering().getGatheringId();

        boolean isMember = gatheringMemberRepo.existsByUserIdAndGatheringId(userId, gatheringId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }
        ScheduleDetailDTO savedScheduleDetailDTO = ScheduleDetailDTO.from(schedule);
        savedScheduleDetailDTO.updateIsSubManger(userId.equals(schedule.getSubManager().getUserId()));
        savedScheduleDetailDTO.updateScheduleAccountBalance(schedule.getScheduleAccount().getAccountBalance());

        return savedScheduleDetailDTO;
    }

    @Transactional
    // 일정 생성
    public Long createSchedule(Long userId, Long gatheringId, ScheduleCreateDTO scheduleCreateDTO) {
        scheduleCreateDTO.updateSubManagerId(userId);
        GatheringMember gatheringMember = gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(gatheringId, userId)
                .orElseThrow(() -> new RuntimeException("gatheringId와 userId로 gatheringMember를 찾을 수 없습니다."));
        Long gatheringDeposit = gatheringMember.getGathering().getGatheringDeposit();

        if (gatheringMember.getGatheringMemberAccountBalance() < scheduleCreateDTO.getPerBudget()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"일정 생성자의 잔액이 인당예산보다 적어 생성이 불가능합니다");
        }

        Gathering gathering = gatheringRepo.findById(gatheringId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gathering not found"));

        System.out.println("11111 부총무 아이디 : " + scheduleCreateDTO.getSubManagerId());
        User subManager = userRepo.findByUserId(scheduleCreateDTO.getSubManagerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubManger not found"));

//      일정 생성후에 계좌를 생성해야하고  부총무가 일정멤버로 지정되어야 함

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

        MakeAccountDTO makeAccountDTO = MakeAccountDTO.builder().accountPw(scheduleCreateDTO.getScheduleAccountPw()).build();

        scheduleAccountService.createAccount(userId, savedSchedule.getId(), makeAccountDTO, scheduleCreateDTO.getPerBudget());

        scheduleMemberService.setSubManager(gatheringId,savedSchedule.getId(),scheduleCreateDTO.getSubManagerId(),scheduleCreateDTO.getPerBudget());

        return savedSchedule.getId();
    }

    @Transactional
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
        Integer penaltyApplyCount = scheduleMemberRepo.countByScheduleIdAndPenaltyAppliedIsAttendFalse(scheduleId);
        Integer attendeeCount = scheduleMemberRepo.countByScheduleIdAndPenaltyNotAppliedIsAttendTrue(scheduleId);
        Integer penaltyRate = schedule.getPenaltyRate();
        Long remainingAmount = scheduleAccount.getAccountBalance();
        Long perBudget = schedule.getPerBudget();

        List<GatheringMember> penaltyAppliedMembers = gatheringMemberRepo.findGatheringMembersByScheduleIdAndPenaltyAppliedIsAttendFalse(scheduleId);
        List<GatheringMember> attendeeMembers = gatheringMemberRepo.findGatheringMembersByScheduleIdAndPenaltyNotAppliedIsAttendTrue(scheduleId);


        // 일정 멤버 삭제
        scheduleMemberRepo.deleteAllByScheduleId(scheduleId);
        System.out.println("일정 멤버 삭제");
        // 일정 삭제
        scheduleRepo.deleteById(scheduleId);

        System.out.println("일정 삭제");
        // 일정 계좌 삭제

        System.out.println("일정 아이디 : " + scheduleId);


        // 돈을 1원도 안썻으면 그냥 페널티 대상자들까지 전부다 인당예산만큼 나눠주기
        if(perBudget*attendeeCount + perBudget*((100-penaltyRate)/100)*penaltyApplyCount == scheduleAccount.getAccountBalance()){
            penaltyAppliedMembers.forEach(member -> member.increaseGatheringMemberAccountBalance(perBudget*((100-penaltyRate)/100)));
            attendeeMembers.forEach(member -> member.increaseGatheringMemberAccountBalance(perBudget));
        }

        else {

            Long attendeeMembersAmount = remainingAmount / attendeeCount; // 참여자가 한명당 받아야하는 금액
            Long smallChangeAmount = remainingAmount % attendeeCount; // 1원 단위로 남은 금액

            attendeeMembers.forEach(member -> member.increaseGatheringMemberAccountBalance(attendeeMembersAmount));

            // 나머지 1원들
            Iterator<GatheringMember> iterator = attendeeMembers.iterator();
            while (smallChangeAmount > 0 && iterator.hasNext()) {
                iterator.next().increaseGatheringMemberAccountBalance(1L);
                smallChangeAmount--;
            }
        }


        // 일정계좌에 남은 금액을 0으로 만듬 (사실 필요없는데 일단 넣어놈)
        scheduleAccount.decreaseBalance(scheduleAccount.getAccountBalance());
        ///////////////////////////////////////////////////////

            scheduleAccountRepo.deleteById(scheduleAccountId);


        }
    }
