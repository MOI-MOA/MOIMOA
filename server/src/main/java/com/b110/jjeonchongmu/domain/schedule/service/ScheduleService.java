package com.b110.jjeonchongmu.domain.schedule.service;

import com.b110.jjeonchongmu.domain.schedule.dto.*;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepo scheduleRepo;

    public List<ScheduleListDTO> getScheduleList() {
        return scheduleRepo.findAllSchedules();
    }

    @Transactional
    public void createSchedule(ScheduleCreateDTO dto) {
        User manager = getCurrentUser();
        Gathering gathering = getCurrentGathering();

        Schedule schedule = Schedule.builder()
                .gathering(gathering)
                .manager(manager)
                .title(dto.getScheduleTitle())
                .detail(dto.getScheduleDetail())
                .place(dto.getSchedulePlace())
                .startTime(dto.getScheduleStartTime())
                .perBudget(dto.getPerBudget())
                .totalBudget(dto.getTotalBudget())
                .penaltyApplyDate(dto.getPenaltyApplyDate())
                .build();

        scheduleRepo.save(schedule);
    }

    public ScheduleDetailDTO getScheduleDetail(Long scheduleId) {
        Long userId = getCurrentUserId();
        return scheduleRepo.findScheduleDetailById(scheduleId, userId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateSchedule(ScheduleUpdateDTO dto) {
        Schedule schedule = scheduleRepo.findById(dto.getScheduleId())
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
        validateManager(schedule);
        schedule.update(dto);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
        validateManager(schedule);
        scheduleRepo.delete(schedule);
    }

    @Transactional
    public void attendSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
        User user = getCurrentUser();
        schedule.addAttendee(user);
    }

    @Transactional
    public void cancelAttendance(Long scheduleId) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
        User user = getCurrentUser();
        schedule.removeAttendee(user);
    }

    public PerBudgetDTO getPerBudget(Long scheduleId) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
        return PerBudgetDTO.builder()
                .perBudget((int) schedule.getPerBudget())
                .build();
    }

    private User getCurrentUser() {
        // SecurityContext에서 현재 사용자 정보를 가져오는 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Long getCurrentUserId() {
        // SecurityContext에서 현재 사용자 ID를 가져오는 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Gathering getCurrentGathering() {
        // 현재 모임 정보를 가져오는 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void validateManager(Schedule schedule) {
        User currentUser = getCurrentUser();
        if (!schedule.getManager().equals(currentUser)) {
            throw new UnauthorizedException("총무만 일정을 수정/삭제할 수 있습니다.");
        }
    }
}
