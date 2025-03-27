package com.b110.jjeonchongmu.domain.gathering.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gathering_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class GatheringMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_member_id", nullable = false)
    private Long gatheringMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User gatheringMemberUser;

    @Column(name = "gathering_attend_count")
    private int gatheringAttendCount;

    @Column(name = "gathering_member_account_balance")
    private int gatheringMemberAccountBalance;

    @Column(name = "gathering_member_account_deposit")
    private int gatheringMemberAccountDeposit;

    @Column(name = "gathering_payment_status")
    private boolean gatheringPaymentStatus;
}