package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepo scheduleRepo;
    private final GatheringRepo gatheringRepo;
    private final UserRepo userRepo;
    private final ScheduleMemberRepo scheduleMemberRepo;
    private final GatheringMemberRepo gatheringMemberRepo;


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
        boolean isMember = scheduleMemberRepo.existsByUserIdAndGatheringId(userId, scheduleId);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        return ScheduleDetailDTO.from(schedule);
    }

    // 일정 생성(총무만)
    public Long createSchedule(Long userId,Long gatheringId ,ScheduleCreateDTO scheduleCreateDTO) {
        boolean isManager = gatheringRepo.existsByUserIdAndGatheringId(userId, gatheringId);
        if (!isManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }

        Gathering gathering = gatheringRepo.findById(gatheringId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gathering not found"));

        User subManager = userRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

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
    public void updateSchedule(Long userId,Long scheduleId,ScheduleUpdateDTO scheduleUpdateDTO) {
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
    public void deleteSchedule(Long userId, Long scheduleId) {
        scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        boolean isSubManager = scheduleRepo.checkExistsByUserIdAndScheduleId(userId, scheduleId);
        if (!isSubManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this schedule");
        }
        
        scheduleRepo.deleteById(scheduleId);
    }
}
