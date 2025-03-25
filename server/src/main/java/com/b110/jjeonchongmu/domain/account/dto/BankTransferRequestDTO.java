package com.b110.jjeonchongmu.domain.account.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BankTransferRequestDTO {
	private final String userKey;
	private final Long toAccountNo;
	private final Long fromAccountNo;
	private final Long amount;
}
