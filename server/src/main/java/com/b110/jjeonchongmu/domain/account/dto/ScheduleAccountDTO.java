package com.b110.jjeonchongmu.domain.account.dto;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleAccountDTO {
    private Long scheduleAccountId;
    private AccountDTO accountDTO;
}