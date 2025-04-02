package com.b110.jjeonchongmu.domain.mypage.dto.auto;

import com.b110.jjeonchongmu.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoPaymentResponse {
    private Long userId;
    private Long accountBalance;
    private List<AutoPaymentDto> autoTransfers = new ArrayList<>();
    public AutoPaymentResponse(User user, Long accountBalance, List<AutoPaymentDto> autoPaymentDtos) {
        this.userId = user.getUserId();
        this.accountBalance = accountBalance;
        this.autoTransfers = autoPaymentDtos;
    }
}