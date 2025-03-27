package com.b110.jjeonchongmu.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MakeGatheringAccountDTO {
	private Long userId;
	private String gatheringAccountNo;
	private Long gatheringId;
	private String accountPw;
}
