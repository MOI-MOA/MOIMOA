package com.b110.jjeonchongmu.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class BankAccountResponseDTO {
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
		private String accountNo;
		private Currency currency;

		@Getter
		@Setter
		@NoArgsConstructor
		@AllArgsConstructor
		@Builder
		public static class Currency {
			private String currency;
			private String currencyName;
		}
	}
}
