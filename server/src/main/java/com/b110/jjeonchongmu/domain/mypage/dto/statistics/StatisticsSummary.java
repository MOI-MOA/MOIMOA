package com.b110.jjeonchongmu.domain.mypage.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsSummary {
    private int totalExpense;
    private int averageMonthlyExpense;
    private String highestGroupExpense;
    private String highestMonthlyExpense;
    private double averageParticipationRate;
    private String highestParticipationRateGroup;
}
