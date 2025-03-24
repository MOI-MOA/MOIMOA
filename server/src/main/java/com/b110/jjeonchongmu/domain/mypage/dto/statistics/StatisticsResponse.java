package com.b110.jjeonchongmu.domain.mypage.dto.statistics;

import com.b110.jjeonchongmu.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsResponse {
    private List<MonthlyExpenseData> monthlyExpenseData;
    private List<GroupExpenseData> groupExpenseData;
    private List<ParticipationRateData> participationRateData;
    private StatisticsSummary statisticsSummary;
} 