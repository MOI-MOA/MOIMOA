package com.b110.jjeonchongmu.domain.main.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateScheduleRequestDTO {
    @NotNull(message = "모임 ID는 필수입니다.")
    private Integer gatheringId;           // 모임 ID

    @NotBlank(message = "일정 제목은 필수입니다.")
    private String scheduleTitle;          // 일정 제목

    @NotBlank(message = "일정 상세 내용은 필수입니다.")
    private String scheduleDetail;         // 일정 상세

    @NotBlank(message = "일정 장소는 필수입니다.")
    private String schedulePlace;          // 일정 장소

    @NotNull(message = "일정 시작 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduleStartTime; // 일정 시작 시간

    @NotNull(message = "1인당 예산은 필수입니다.")
    private Long perBudget;               // 1인당 예산
} 