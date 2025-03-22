package com.b110.jjeonchongmu.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsResponse {
    private StatisticsData data;
    private List<GatheringStatistics> datas;
    private List<MonthlyStatistics> month;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StatisticsData {
        private Long totalExpences;
        private Long averageMonthExpences;
        private String highestExpencesGathering;
        private int highestExpencesMonth;
        private float averageAttendPercent;
        private String highestAttendGathering;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GatheringStatistics {
        private String gatheringName;
        private int scheduleCount;
        private int scheduleAttendCount;
        private Long gatheringExpenses;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MonthlyStatistics {
        private int month;
        private Long monthExpences;
    }
} 