package com.b110.jjeonchongmu.domain.main.service;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.repo.MainRepo;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDTO;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleListDTO;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 메인 화면 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {
    private final MainRepo mainRepo;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 메인 홈 화면 정보 조회
     * - 미확인 일정 수
     * - 오늘의 일정
     * - 다가오는 일정
     */
    public List<MainHomeResponseDTO> getMainHome(Long userId) {
        // 오늘의 일정과 다가오는 일정 조회

        return List.of();
    }

    /**
     * 미확인 일정 목록 조회
     */
    public List<ScheduleListDTO> getUncheckSchedules(Long userId) {

        return List.of();
    }

    /**
     * 개인 일정 목록 조회
     */
    public List<ScheduleListDTO> getPersonalSchedules(Long userId) {

        return List.of();
    }

    /**
     * 월별 일정 조회
     * /api/v1/main/schedule/{year}/{month}
     *
     */
    public List<DateDTO> getMonthSchedules(Long userId, int year, int month) {
        // 해당 월에 일정이 있는 날짜들을 조회


        // 날짜만 추출하여 DateInfo 리스트로 변환

        return List.of();
    }

    /**
     * 특정 날짜의 일정 조회 수정 필요. userId는 jwt에서 가져오도록.
     */
    public List<ScheduleListDTO> getDaySchedules(Long userId, int year, int month, int date) {

    }

    /**
     * 오늘의 일정 조회
     */
       public List<ScheduleListDTO> getTodaySchedules(Long userId) {
        List<Object[]> scheduleData = mainRepo.findTodaySchedules();
        List<ScheduleListDTO> result = new ArrayList<>();

        for (Object[] data : scheduleData) {
            ScheduleListDTO schedule = new ScheduleListDTO();

            // 데이터베이스에서 조회한 결과를 기반으로 DTO 설정
            if (data.length > 0) schedule.setScheduleId((Long) data[0]);
            if (data.length > 1) schedule.setScheduleTitle((String) data[1]);
            if (data.length > 2) schedule.setScheduleDetail((String) data[2]);
            if (data.length > 3) schedule.setSchedulePlace((String) data[3]);
            if (data.length > 4) schedule.setScheduleStartTime((LocalDateTime) data[4]);
            if (data.length > 5) schedule.setGatheringId((Long) data[5]);
            if (data.length > 6) schedule.setGatheringName((String) data[6]);
            if (data.length > 7) schedule.setAttendeeCount((Integer) data[7]);

            result.add(schedule);
        }

        return result;
    }
}
