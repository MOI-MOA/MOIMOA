package com.b110.jjeonchongmu.domain.main.service;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.repo.MainRepo;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 메인 화면 관련 서비스
 */
@Slf4j
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
        log.debug("===== getMainHome 시작 - 사용자 ID: {} =====", userId);

        // 미확인 일정 수 조회
        int uncheckCount = mainRepo.countUncheckSchedules(userId);
        log.debug("미확인 일정 수: {}", uncheckCount);

        // 오늘의 일정 조회
        List<ScheduleDTO> todaySchedules = getTodaySchedules(userId);
        log.debug("오늘 일정 개수: {}", todaySchedules.size());

        // 이번 달 일정이 있는 날짜 조회
        LocalDate now = LocalDate.now();
        log.debug("현재 날짜: {}", now);
        List<DateDTO> dateList = getMonthSchedules(userId, now.getYear(), now.getMonthValue());
        log.debug("이번 달 일정이 있는 날짜 수: {}", dateList.size());

        // 다가오는 일정 조회
        List<Object[]> upcomingData = mainRepo.findUpcomingSchedules(userId);
        log.debug("다가오는 일정 원본 데이터 개수: {}", upcomingData.size());
        List<ScheduleDTO> upcomingSchedules = convertToScheduleListDTO(upcomingData);
        log.debug("다가오는 일정 변환 후 개수: {}", upcomingSchedules.size());

        // 응답 생성
        MainHomeResponseDTO response = new MainHomeResponseDTO();
        response.setUncheckScheduleCount(uncheckCount);
        response.setDateList(dateList);
        response.setTodayScheduleList(todaySchedules);
        response.setUpcomingScheduleList(upcomingSchedules);

        log.debug("===== getMainHome 완료 =====");
        return response;
    }

    /**
     * 미확인 일정 목록 조회
     */
    public List<ScheduleDTO> getUncheckSchedules(Long userId) {
        log.debug("===== getUncheckSchedules 시작 - 사용자 ID: {} =====", userId);
        List<Object[]> datas = mainRepo.findUncheckSchedules(userId);
        log.debug("미확인 일정 원본 데이터 개수: {}", datas.size());

        if (datas.isEmpty()) {
            log.debug("미확인 일정이 없습니다.");
        } else {
            log.debug("첫 번째 미확인 일정 데이터 구조 확인:");
            Object[] firstRow = datas.get(0);
            for (int i = 0; i < firstRow.length; i++) {
                log.debug("인덱스 {}: {} ({})", i,
                        (firstRow[i] != null ? firstRow[i].toString() : "null"),
                        (firstRow[i] != null ? firstRow[i].getClass().getSimpleName() : "null"));
            }
        }

        List<ScheduleDTO> result = convertToScheduleListDTO(datas);
        log.debug("미확인 일정 변환 후 개수: {}", result.size());
        log.debug("===== getUncheckSchedules 완료 =====");
        return result;
    }

    /**
     * 개인 일정 목록 조회
     */
    public List<ScheduleDTO> getPersonalSchedules(Long userId) {
        log.debug("===== getPersonalSchedules 시작 - 사용자 ID: {} =====", userId);
        List<Object[]> data = mainRepo.findPersonalSchedules(userId);
        log.debug("개인 일정 원본 데이터 개수: {}", data.size());
        List<ScheduleDTO> result = convertToScheduleListDTO(data);
        log.debug("개인 일정 변환 후 개수: {}", result.size());
        log.debug("===== getPersonalSchedules 완료 =====");
        return result;
    }

    /**
     * 월별 일정 조회
     */
    public List<DateDTO> getMonthSchedules(Long userId, int year, int month) {
        log.debug("===== getMonthSchedules 시작 - 사용자 ID: {}, 년/월: {}/{} =====", userId, year, month);
        List<Object[]> data = mainRepo.findMonthSchedules(userId, year, month);
        log.debug("월별 일정 원본 데이터 개수: {}", data.size());
        List<DateDTO> result = new ArrayList<>();

        for (Object[] row : data) {
            if (row.length >= 2) {
                DateDTO dateDTO = new DateDTO();
                dateDTO.setDate(((Number) row[0]).intValue());
                result.add(dateDTO);
            }
        }

        log.debug("월별 일정 변환 후 개수: {}", result.size());
        log.debug("===== getMonthSchedules 완료 =====");
        return result;
    }

    /**
     * 특정 날짜의 일정 조회
     */
    public List<ScheduleDTO> getDaySchedules(Long userId, int year, int month, int date) {
        log.debug("===== getDaySchedules 시작 - 사용자 ID: {}, 날짜: {}/{}/{} =====", userId, year, month, date);
        LocalDate targetDate = LocalDate.of(year, month, date);
        List<Object[]> data = mainRepo.findDaySchedules(userId, targetDate);
        log.debug("일별 일정 원본 데이터 개수: {}", data.size());
        List<ScheduleDTO> result = convertToScheduleListDTO(data);
        log.debug("일별 일정 변환 후 개수: {}", result.size());
        log.debug("===== getDaySchedules 완료 =====");
        return result;
    }

    /**
     * 오늘의 일정 조회
     */
    public List<ScheduleDTO> getTodaySchedules(Long userId) {
        log.debug("===== getTodaySchedules 시작 - 사용자 ID: {} =====", userId);
        List<Object[]> data = mainRepo.findTodaySchedules(userId);
        log.debug("오늘 일정 원본 데이터 개수: {}", data.size());
        List<ScheduleDTO> result = convertToScheduleListDTO(data);
        log.debug("오늘 일정 변환 후 개수: {}", result.size());
        log.debug("===== getTodaySchedules 완료 =====");
        return result;
    }

    /**
     * Object[] 데이터를 ScheduleListDTO로 변환
     */
    private List<ScheduleDTO> convertToScheduleListDTO(List<Object[]> datas) {
        log.debug("===== convertToScheduleListDTO 시작 - 데이터 개수: {} =====", datas.size());
        List<ScheduleDTO> result = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < datas.size(); rowIndex++) {
            Object[] row = datas.get(rowIndex);
            ScheduleDTO dto = new ScheduleDTO();

            // Object[] 배열의 각 컬럼에 해당하는 값을 DTO에 설정
            if (row.length > 0 && row[0] != null) dto.setScheduleId((Long) row[0]);
            if (row.length > 1 && row[1] != null) dto.setScheduleTitle((String) row[1]);
            if (row.length > 3 && row[3] != null) dto.setSchedulePlace((String) row[3]);
            if (row.length > 4 && row[4] != null) {
                java.sql.Timestamp timestamp = (java.sql.Timestamp) row[4];
                dto.setScheduleStartTime(timestamp.toLocalDateTime());
            }
            if (row.length > 5 && row[5] != null) dto.setGatheringId((Long) row[5]);
            if (row.length > 6 && row[6] != null) dto.setGatheringName((String) row[6]);
            if (row.length > 7 && row[7] != null) dto.setAttendeeCount(((Number) row[7]).intValue());
            if (row.length > 8 && row[8] != null) {
                dto.setPerBudget(((Number) row[8]).longValue());
                log.debug("일정 #{} 인당 예산: {}", rowIndex, ((Number) row[8]).longValue());
            } else {
                log.debug("일정 #{} 인당 예산 정보 없음. row.length: {}", rowIndex, row.length);
            }

            result.add(dto);

            if (rowIndex == 0 || rowIndex == datas.size() - 1) {
                log.debug("일정 #{} 변환 결과: ID={}, 제목={}, 장소={}, 시작시간={}, 모임ID={}, 모임명={}, 참석자수={}, 인당예산={}",
                        rowIndex,
                        dto.getScheduleId(),
                        dto.getScheduleTitle(),
                        dto.getSchedulePlace(),
                        dto.getScheduleStartTime(),
                        dto.getGatheringId(),
                        dto.getGatheringName(),
                        dto.getAttendeeCount(),
                        dto.getPerBudget());
            }
        }

        log.debug("===== convertToScheduleListDTO 완료 - 변환된 DTO 개수: {} =====", result.size());
        return result;
    }
}