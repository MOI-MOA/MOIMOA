package com.b110.jjeonchongmu.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequestDTO {

	private AccountType fromAccountType;
	private Long fromAccountId;
	private AccountType toAccountType;
	private Long toAccountId;
	private String tradeDetail;
	private Long transferAmount;
}