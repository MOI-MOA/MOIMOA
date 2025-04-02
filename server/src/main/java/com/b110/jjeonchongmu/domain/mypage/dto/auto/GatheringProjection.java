package com.b110.jjeonchongmu.domain.mypage.dto.auto;

import lombok.Getter;

@Getter
public class GatheringProjection {

	private final Long gatheringId;
	private final String gatheringName;
	private final String depositDate;
	private final Long basicFee;
	private final Long gatheringDeposit;
	private final String accountNo;
	private final Long accountBalance;
	public GatheringProjection(Long gatheringId, String gatheringName, String depositDate,
			Long basicFee, Long gatheringDeposit, String accountNo, Long accountBalance) {
		this.gatheringId = gatheringId;
		this.gatheringName = gatheringName;
		this.depositDate = depositDate;
		this.basicFee = basicFee;
		this.gatheringDeposit = gatheringDeposit;
		this.accountNo = accountNo;
		this.accountBalance = accountBalance;
	}
}
