package com.b110.jjeonchongmu.domain.trade.entity;
import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.account.entity.Account;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "trade")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;
    // Account 3개

    @Column(name = "from_account_type", nullable = false)
    private AccountType fromAccountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(name = "to_account_type", nullable = false)
    private AccountType toAccountType;

    @Column(name = "trade_amount", nullable = true)
    private Long tradeAmount;

    @Column(name = "trade_time", nullable = true)
    private LocalDateTime tradeTime;

    @Column(name = "trade_detail", nullable = true)
    private String tradeDetail;

    @Column(name = "trade_balance", nullable = true)
    private Long tradeBalance;


}