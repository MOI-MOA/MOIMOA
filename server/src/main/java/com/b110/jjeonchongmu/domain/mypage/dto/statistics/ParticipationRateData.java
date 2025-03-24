package com.b110.jjeonchongmu.domain.mypage.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRateData {
    private String name;
    private int attendedSchedules;
    private int totalSchedules;
    private int rate;

    public ParticipationRateData(String name, int attendedSchedules, int totalSchedules) {
        this.name = name;
        this.attendedSchedules = attendedSchedules;
        this.totalSchedules = totalSchedules;
        this.rate = attendedSchedules / totalSchedules * 100;
    }
}
