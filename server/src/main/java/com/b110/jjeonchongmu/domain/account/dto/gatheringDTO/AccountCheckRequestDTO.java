package com.b110.jjeonchongmu.domain.account.dto.gatheringDTO;


import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCheckRequestDTO {
    private String accountNo;
}