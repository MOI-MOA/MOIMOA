package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringDetailSchedules {
    private Long id;
    private String name;
    private LocalDateTime date;
    private Integer participants;
    private Long budgetPerPerson;
    private Long totalBudget;
    private String location;
    private Boolean isChecked;
    private Boolean isAttend;
    private boolean isSubManager;
    private Long scheduleAccountBalance;


    public void updateIsSubManager(boolean isSubmanager){this.isSubManager = isSubManager;}
}
