package com.b110.jjeonchongmu.domain.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MainHomeResponse {
    private int uncheckScheduleCount;
    private List<DateDto> dateList;
    private List<ScheduleDto> todayScheduleList;
    private List<ScheduleDto> upcommingScheduleList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DateDto {
        private int date;
    }
} 