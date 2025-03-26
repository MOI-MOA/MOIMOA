package com.b110.jjeonchongmu.domain.account.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MakeExternalAccountDTO {
	private final String userKey;
	private final String externalAccountType;
	private final Integer accountPw;
}
