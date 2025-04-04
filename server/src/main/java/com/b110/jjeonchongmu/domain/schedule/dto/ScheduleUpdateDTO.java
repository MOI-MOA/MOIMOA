package com.b110.jjeonchongmu.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 일정 수정하는 페이지 아직 없지만, 추후 수정 시 필요.
// 일단은 보류작업.

public class ScheduleUpdateDTO {
    private Long scheduleId;
    private String scheduleTitle;
    private String scheduleDetail;
    private String schedulePlace;
}