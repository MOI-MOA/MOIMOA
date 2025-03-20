package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trade")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "from_account_type", nullable = false)
    private Long fromAccountType;

    @Column(name = "to_account_id", nullable = false)
    private Long toAccountId;

    @Column(name = "to_account_type", nullable = false)
    private Long toAccountType;

    @Column(name = "trade_amount", nullable = true)
    private Integer tradeAmount;

    @Column(name = "trade_time", nullable = true)
    private LocalDateTime tradeTime;

    @Column(name = "trade_detail", nullable = true)
    private String tradeDetail;
}