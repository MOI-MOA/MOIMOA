package com.b110.jjeonchongmu.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountCheckResponseDTO {

	private String toAccountNo;
	private Long amount;
	private Boolean isAccount;
}
