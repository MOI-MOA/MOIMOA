package com.b110.jjeonchongmu.domain.account.dto;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleAccountDTO {
    private Long scheduleAccountId;
    private Long scheduleId;
    private Long scheduleAccountNameId;
    private Long scheduleAccountNo;
    private Integer scheduleAccountBalance;
}