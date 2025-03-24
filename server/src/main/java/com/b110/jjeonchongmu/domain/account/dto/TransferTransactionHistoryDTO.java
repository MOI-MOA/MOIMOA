package com.b110.jjeonchongmu.domain.account.dto;

import com.b110.jjeonchongmu.domain.account.enums.TransactionStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferTransactionHistoryDTO {
	private String transactionId;
	private Long fromAccountId;
	private AccountType fromAccountType;
	private Long toAccountId;
	private AccountType toAccountType;
	private Long amount;
	private String detail;
	private TransactionStatus status;
	private String errorCode;
	private String errorMessage;
	private String externalTransactionId;
	private LocalDateTime createdAt;
	private LocalDateTime completedAt;
}
