package com.b110.jjeonchongmu.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponseDTO {
	@JsonProperty("Header")
	private Header header;
	@JsonProperty("REC")
	private Rec rec;

	@Getter
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
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Rec {
		private String bankCode;
		private String accountNo;
		private Currency currency;

		@Getter
		@NoArgsConstructor
		@AllArgsConstructor
		@Builder
		public static class Currency {
			private String currency;
			private String currencyName;
		}
	}
}
