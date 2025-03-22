package com.b110.jjeonchongmu.domain.main.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 일정 목록 응답 DTO
 */
@Getter
@NoArgsConstructor
public class ScheduleListResponseDTO {
    private List<ScheduleDTO> datas;    // 일정 목록 (API 명세에 맞춰 datas로 명명)

    @Builder
    public ScheduleListResponseDTO(List<ScheduleDTO> datas) {
        this.datas = datas;
    }
} 