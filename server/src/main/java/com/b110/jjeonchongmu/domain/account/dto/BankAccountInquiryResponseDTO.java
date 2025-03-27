package com.b110.jjeonchongmu.domain.account.dto;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class BankAccountInquiryResponseDTO {
	private Header header;
	private Rec rec;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Header {
		private String responseCode;
		private String responseMessage;
		private String apiName;
		private String transmissionDate;
		private String transmissionTime;
		private String institutionCode;
		private String apiKey;
		private String apiServiceCode;
		private String institutionTransactionUniqueNo;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Rec {
		private String bankCode;
		private String bankName;
		private String userName;
		private String accountNo;
		private String accountName;
		private String accountTypeCode;
		private String accountTypeName;
		private String accountCreateDate;
		private String accountExpiryDate;
		private String dailyTransferLimit;
		private String oneTimeTransferLimit;
		private String accountBalance;
		private String lastTransactionDate;
		private String currency;
	}
}
