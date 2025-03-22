package com.b110.jjeonchongmu.domain.main.dto;

import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메인 홈 화면 응답 DTO
 * - 사용자의 모임 및 일정 정보를 포함
 */
@Getter
@NoArgsConstructor
public class MainHomeResponseDTO {
    
    /**
     * 모임 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class GatheringDTO {
        private Long gatheringId;          // 모임 ID
        private String gatheringName;      // 모임 이름
        private Integer memberCount;        // 모임 멤버 수
        private LocalDateTime createdAt;    // 모임 생성일시

        @Builder
        public GatheringDTO(Long gatheringId, String gatheringName, Integer memberCount, LocalDateTime createdAt) {
            this.gatheringId = gatheringId;
            this.gatheringName = gatheringName;
            this.memberCount = memberCount;
            this.createdAt = createdAt;
        }

        public static GatheringDTO from(Gathering gathering) {
            return GatheringDTO.builder()
                    .gatheringId(gathering.getGatheringId())
                    .gatheringName(gathering.getGatheringName())
                    .memberCount(gathering.getGatheringMembers().size())
                    .createdAt(gathering.getCreatedAt())
                    .build();
        }
    }

    /**
     * 일정 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class ScheduleDTO {
        private Long scheduleId;           // 일정 ID
        private String scheduleName;       // 일정 이름
        private LocalDateTime startDate;    // 시작일시
        private LocalDateTime endDate;      // 종료일시

        @Builder
        public ScheduleDTO(Long scheduleId, String scheduleName, LocalDateTime startDate, LocalDateTime endDate) {
            this.scheduleId = scheduleId;
            this.scheduleName = scheduleName;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public static ScheduleDTO from(Schedule schedule) {
            return ScheduleDTO.builder()
                    .scheduleId(schedule.getScheduleId())
                    .scheduleName(schedule.getScheduleName())
                    .startDate(schedule.getStartDate())
                    .endDate(schedule.getEndDate())
                    .build();
        }
    }

    private List<GatheringDTO> gatherings;     // 사용자의 모임 목록
    private List<ScheduleDTO> schedules;       // 사용자의 일정 목록

    @Builder
    public MainHomeResponseDTO(List<GatheringDTO> gatherings, List<ScheduleDTO> schedules) {
        this.gatherings = gatherings;
        this.schedules = schedules;
    }
} 