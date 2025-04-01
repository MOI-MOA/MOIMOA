package com.b110.jjeonchongmu.domain.gathering.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gathering_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Integer gatheringAttendCount;

    @Column(name = "gathering_member_account_balance")
    private Integer gatheringMemberAccountBalance;

    @Column(name = "gathering_member_account_deposit")
    private Integer gatheringMemberAccountDeposit;

    @Column(name = "gathering_payment_status")
    private boolean gatheringPaymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "gathering_member_status")
    private GatheringMemberStatus gatheringMemberStatus;

    @Builder
    public GatheringMember(Gathering gathering, User gatheringMemberUser, Integer gatheringAttendCount,
                           Integer gatheringMemberAccountBalance, Integer gatheringMemberAccountDeposit,
                           boolean gatheringPaymentStatus, GatheringMemberStatus gatheringMemberStatus) {
        this.gathering = gathering;
        this.gatheringMemberUser = gatheringMemberUser;
        this.gatheringAttendCount = gatheringAttendCount;
        this.gatheringMemberAccountBalance = gatheringMemberAccountBalance;
        this.gatheringMemberAccountDeposit = gatheringMemberAccountDeposit;
        this.gatheringPaymentStatus = gatheringPaymentStatus;
        this.gatheringMemberStatus = gatheringMemberStatus != null ? gatheringMemberStatus : GatheringMemberStatus.PENDING;
    }

    public void updateStatus(GatheringMemberStatus status) {
        this.gatheringMemberStatus = status;
    }
}
