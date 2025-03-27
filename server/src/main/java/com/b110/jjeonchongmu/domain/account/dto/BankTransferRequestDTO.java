package com.b110.jjeonchongmu.domain.account.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BankTransferRequestDTO {
	private final String userKey;
	private final String toAccountNo;
	private final String fromAccountNo;
	private final Long amount;
}
