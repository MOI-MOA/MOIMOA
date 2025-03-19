package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id", nullable = false)
    private Long groupMemberId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private PersonalAccount.User user;

    @Column(name = "group_attend_count")
    private Integer groupAttendCount;

    @Column(name = "group_member_account_balance")
    private Integer groupMemberAccountBalance;

    @Column(name = "group_member_account_deposit")
    private Integer groupMemberAccountDeposit;

    @Column(name = "group_payment_status")
    private Boolean groupPaymentStatus;

    @Entity
    @Table(name = "trade")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Trade {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "trade_id", nullable = false)
        private Long tradeId;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private PersonalAccount.User user;

        @Column(name = "trade_type", nullable = false)
        private String tradeType;

        @Column(name = "account_type", nullable = false)
        private String accountType;

        @Column(name = "from_account_id", nullable = false)
        private Long fromAccountId;

        @Column(name = "to_account_id", nullable = false)
        private Long toAccountId;

        @Column(name = "trade_amount", nullable = true)
        private Integer tradeAmount;

        @Column(name = "trade_time", nullable = true)
        private LocalDateTime tradeTime;

        @Column(name = "trade_detail", nullable = true)
        private String tradeDetail;
    }
}