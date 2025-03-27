package com.b110.jjeonchongmu.domain.mypage.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupExpenseData {
    private String name;
    private Long amount;
}
