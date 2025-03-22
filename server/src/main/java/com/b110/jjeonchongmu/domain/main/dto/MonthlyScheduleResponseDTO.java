package com.b110.jjeonchongmu.domain.main.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MonthlyScheduleResponseDTO {
    private Integer year;                  // 연도
    private Integer month;                 // 월
    private List<ScheduleDTO> schedules;   // 해당 월의 일정 목록

    @Builder
    public MonthlyScheduleResponseDTO(Integer year, Integer month, List<ScheduleDTO> schedules) {
        this.year = year;
        this.month = month;
        this.schedules = schedules;
    }
} 