package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddGatheringDTO {

	//모임 이름
	private String gatheringName; //
	//모임 소개
	private String gatheringIntroduction; //
	//참여 인원
	private int memberCount; // 기본값 0에서 자동으로 추가.
	//페이백 퍼센트
	private int penaltyRate;
	//매월 입금일
	private String depositDate;
	//계좌 비밀번호
	private int gatheringAccountPW; // 모임계좌 비밀번호

	private long basicFee;

	private long gatheringDeposit;

}
