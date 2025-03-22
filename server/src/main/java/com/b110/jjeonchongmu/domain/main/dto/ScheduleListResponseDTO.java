package com.b110.jjeonchongmu.domain.main.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 일정 목록 응답 DTO
 * - 일정 목록을 포함
 */
@Getter
@NoArgsConstructor
public class ScheduleListResponseDTO {
    private List<ScheduleDTO> schedules;    // 일정 목록

    @Builder
    public ScheduleListResponseDTO(List<ScheduleDTO> schedules) {
        this.schedules = schedules;
    }
} 