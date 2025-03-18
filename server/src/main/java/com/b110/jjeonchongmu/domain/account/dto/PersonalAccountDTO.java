package com.b110.jjeonchongmu.domain.account.dto;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalAccountDTO {
    private Long personalAccountId;
    private String personalAccountNo;
    private Integer personalAccountBalance;
}