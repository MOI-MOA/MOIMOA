package com.b110.jjeonchongmu.domain.schedule.service;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepo scheduleRepo;
    private final GatheringRepo gatheringRepo;
    private final UserRepo userRepo;
    private final ScheduleMemberRepo scheduleMemberRepo;



    // 모임 일정목록 조회
    public List<ScheduleDTO> getScheduleList(Long userId, Long gatheringId) {
        return scheduleRepo.findByGatheringGatheringId(gatheringId)
                .stream()
                .map(ScheduleDTO::from)
                .collect(Collectors.toList());
    }

    // 일정 상세조회
    public ScheduleDetailDTO getScheduleDetail(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        return ScheduleDetailDTO.from(schedule);
    }

    // 일정 생성(총무만)
    @Transactional
    public void createSchedule(Long userId,Long gatheringId ,ScheduleCreateDTO scheduleCreateDTO) {
        Gathering gathering = gatheringRepo.findById(gatheringId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gathering not found"));

        User subManager = userRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


//        private ScheduleAccount scheduleAccount;
//        일정 계좌번호 받아오는 로직 필요
//        private List<ScheduleMember> attendees;
//        일정 멤버들 받아오는 로직 필요

//        생성과 동시에 계좌를 생성해야하고 총무, 부총무가 일정멤버로 지정되어야 함

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

        scheduleRepo.save(schedule);

    }
    // 일정 수정(총무만)
    @Transactional
    public void updateSchedule(Long userId,Long scheduleId,ScheduleUpdateDTO scheduleUpdateDTO) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found"));

        schedule.updateTitle(scheduleUpdateDTO.getScheduleTitle());
        schedule.updateTitle(scheduleUpdateDTO.getScheduleTitle());
        schedule.updateTitle(scheduleUpdateDTO.getScheduleTitle());
        schedule.updateTitle(scheduleUpdateDTO.getScheduleTitle());

    }
    // 일정 삭제(총무만)
    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
//        Schedule schedule = scheduleRepo.findById(scheduleId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
//
//        // 삭제 권한 확인 (옵션)
//        if (!schedule.getSubManager().getUserId().equals(userId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only submanager can delete schedule");
//        }
        
        scheduleRepo.deleteById(scheduleId);
    }
}
