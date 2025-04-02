package com.b110.jjeonchongmu.domain.account.dto;
import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoPaymentDTO {
    private Long autoPaymentId;
    private Long personalAccountId;
    private Long gatheringAccountId;
    private Long autoPaymentAmount;
    private Integer autoPaymentDate;
    private Boolean isActive;

    public static AutoPaymentDTO from(AutoPayment entity) {
        return AutoPaymentDTO.builder()
                .autoPaymentId(entity.getAutoPaymentId())
                .personalAccountId(entity.getPersonalAccount().getAccountId())
                .gatheringAccountId(entity.getGatheringAccount().getAccountId())
                .autoPaymentAmount(entity.getAutoPaymentAmount())
                .autoPaymentDate(entity.getAutoPaymentDate())
                .isActive(entity.getIsActive())
                .build();
    }

    public AutoPayment toEntity(PersonalAccount personalAccount, GatheringAccount gatheringAccount) {
        return AutoPayment.builder()
                .personalAccount(personalAccount)
                .gatheringAccount(gatheringAccount)
                .autoPaymentAmount(this.autoPaymentAmount)
                .autoPaymentDate(this.autoPaymentDate)
                .isActive(this.isActive)
                .build();
    }
}