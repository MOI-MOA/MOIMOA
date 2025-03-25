package com.b110.jjeonchongmu.domain.main.service;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.repo.MainRepo;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 메인 화면 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {

    private final MainRepo mainRepo;

    /**
     * 메인 홈 화면 정보 조회
     * - 미확인 일정 수
     * - 오늘의 일정
     * - 다가오는 일정
     */
    public MainHomeResponseDTO getMainHome(Long userId) {
        // 미확인 일정 수 조회
        int uncheckCount = mainRepo.countUncheckSchedules(userId);

        // 오늘의 일정 조회
        List<ScheduleDTO> todaySchedules = getTodaySchedules(userId);

        // 이번 달 일정이 있는 날짜 조회
        LocalDate now = LocalDate.now();
        List<DateDTO> dateList = getMonthSchedules(userId, now.getYear(), now.getMonthValue());

        // 다가오는 일정 조회
        List<Object[]> upcomingData = mainRepo.findUpcomingSchedules(userId);
        List<ScheduleDTO> upcomingSchedules = convertToScheduleListDTO(upcomingData);

        // 응답 생성
        MainHomeResponseDTO response = new MainHomeResponseDTO();
        response.setUncheckScheduleCount(uncheckCount);
        response.setDateList(dateList);
        response.setTodayScheduleList(todaySchedules);
        response.setUpcomingScheduleList(upcomingSchedules);

        return response;
    }

    /**
     * 미확인 일정 목록 조회
     */
    public List<ScheduleDTO> getUncheckSchedules(Long userId) {
        List<Object[]> datas = mainRepo.findUncheckSchedules(userId);
        return convertToScheduleListDTO(datas);
    }

    /**
     * 개인 일정 목록 조회
     */
    public List<ScheduleDTO> getPersonalSchedules(Long userId) {
        List<Object[]> data = mainRepo.findPersonalSchedules(userId);
        return convertToScheduleListDTO(data);
    }

    /**
     * 월별 일정 조회
     */
    public List<DateDTO> getMonthSchedules(Long userId, int year, int month) {
        List<Object[]> data = mainRepo.findMonthSchedules(userId, year, month);
        List<DateDTO> result = new ArrayList<>();

        for (Object[] row : data) {
            if (row.length >= 2) {
                DateDTO dateDTO = new DateDTO();
                dateDTO.setDate((Integer) row[0]); // DAY(s.start_time)
                result.add(dateDTO);
            }
        }

        return result;
    }

    /**
     * 특정 날짜의 일정 조회
     */
    public List<ScheduleDTO> getDaySchedules(Long userId, int year, int month, int date) {
        LocalDate targetDate = LocalDate.of(year, month, date);
        List<Object[]> data = mainRepo.findDaySchedules(userId, targetDate);
        return convertToScheduleListDTO(data);
    }

    /**
     * 오늘의 일정 조회
     */
    public List<ScheduleDTO> getTodaySchedules(Long userId) {
        List<Object[]> data = mainRepo.findTodaySchedules(userId);
        return convertToScheduleListDTO(data);
    }

    /**
     * Object[] 데이터를 ScheduleListDTO로 변환
     */
    private List<ScheduleDTO> convertToScheduleListDTO(List<Object[]> datas) {
        List<ScheduleDTO> result = new ArrayList<>();

        for (Object[] row : datas) {
            ScheduleDTO dto = new ScheduleDTO();

            // Object[] 배열의 각 컬럼에 해당하는 값을 DTO에 설정
            if (row.length > 0 && row[0] != null) dto.setScheduleId((Long) row[0]);
            if (row.length > 1 && row[1] != null) dto.setScheduleTitle((String) row[1]);
            if (row.length > 3 && row[3] != null) dto.setSchedulePlace((String) row[3]);
            if (row.length > 4 && row[4] != null) dto.setScheduleStartTime((LocalDateTime) row[4]);
            if (row.length > 5 && row[5] != null) dto.setGatheringId((Long) row[5]);
            if (row.length > 6 && row[6] != null) dto.setGatheringName((String) row[6]);
            if (row.length > 7 && row[7] != null) dto.setAttendeeCount(((Number) row[7]).intValue());
            if (row.length > 8 && row[8] != null) dto.setPerBudget(((Number) row[8]).longValue());

            result.add(dto);
        }

        return result;
    }
}