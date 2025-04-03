package com.b110.jjeonchongmu.domain.mypage.dto.auto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAutoPaymentRequestDto {
//    private Long id; // 자동이체 id
    private int amount; // 가격
    private int day;
    private boolean status; // 활성여부 "active" | "inactive"
}
