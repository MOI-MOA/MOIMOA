package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.schedule.dto.*;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepo scheduleRepo;

    // 모임 일정목록 조회
    public List<ScheduleDTO> getScheduleList(Long userId,Long gatheringId) {
        return scheduleRepo.findByGatheringGatheringId(gatheringId)
                .stream()
                .map(schedule -> ScheduleDTO.from(schedule, scheduleRepo))
                .collect(Collectors.toList());
    }
    // 일정 상세조회
    public ScheduleDetailDTO getScheduleDetail(Long userId,Long scheduleId) {
        return scheduleRepo.findScheduleDetailById(scheduleId);
    }
    // 일정 멤버(참여자) 목록 조회
    public List<ScheduleMemberDTO> getScheduleMember(Long userId,Long scheduleId){
        return scheduleRepo.selectAllScheduleMemebers(scheduleId);
    };
    // 일정 생성(총무만)
    @Transactional
    public void createSchedule(Long userId,Long gatheringId ,ScheduleCreateDTO scheduleCreateDTO) {

        scheduleRepo.insertSchedule(gatheringId,scheduleCreateDTO);
    }
    // 일정 수정(총무만)
    @Transactional
    public void updateSchedule(Long userId,Long scheduleId,ScheduleUpdateDTO scheduleUpdateDTO) {
        scheduleRepo.updateSchedule(scheduleId,scheduleUpdateDTO);

    }
    // 일정 삭제(총무만)
    @Transactional
    public void deleteSchedule(Long userId,Long scheduleId) {
        scheduleRepo.deleteSchedule(scheduleId);
    }
}
