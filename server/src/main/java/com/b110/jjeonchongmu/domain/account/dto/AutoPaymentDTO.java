package com.b110.jjeonchongmu.domain.account.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoPaymentDTO {
    private Long autoPaymentId;
    private Long personalAccountId;
    private Long groupAccountId;
    private Integer autoPaymentAmount;
    private LocalDateTime autoPaymentDate;
    private Boolean isActive;
}