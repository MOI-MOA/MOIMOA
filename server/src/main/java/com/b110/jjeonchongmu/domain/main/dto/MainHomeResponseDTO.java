package com.b110.jjeonchongmu.domain.main.dto;

import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 메인 홈 화면 응답 DTO
 * - 사용자의 미확인 일정 수
 * - 이번달 일정이 있는 날짜 목록
 * - 오늘 일정 목록
 * - 다가오는 일정 목록
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainHomeResponseDTO {

    private int uncheckScheduleCount;
    private List<DateDTO> dateList;
    private List<ScheduleDTO> todayScheduleList;
    private List<ScheduleDTO> upcommingScheduleList;
}