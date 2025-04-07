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
    private Long gatheringMemberAccountBalance;

    @Column(name = "gathering_member_account_deposit")
    private Long gatheringMemberAccountDeposit;

    @Column(name = "gathering_payment_status")
    private Boolean gatheringPaymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "gathering_member_status")
    private GatheringMemberStatus gatheringMemberStatus;

    @Builder
    public GatheringMember(Gathering gathering, User gatheringMemberUser, Integer gatheringAttendCount,

                          Long gatheringMemberAccountBalance, Long gatheringMemberAccountDeposit,
                          Boolean gatheringPaymentStatus, GatheringMemberStatus gatheringMemberStatus) {
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

    public void decreaseGatheringMemberAccountBalance(Long amount) {
        this.gatheringMemberAccountBalance -= amount;
    }
    public void increaseGatheringMemberAccountBalance(Long amount) {
        this.gatheringMemberAccountBalance += amount;
    }

    public void decreaseGatheringMemberAccountDeposit(Long amount) { this.gatheringMemberAccountDeposit -= amount; }
    public void increaseGatheringMemberAccountDeposit(Long amount) { this.gatheringMemberAccountDeposit += amount; }

	public void updateGatheringMemberAccountDeposit(long gatheringMemberAccountDeposit) {
        this.gatheringMemberAccountDeposit = gatheringMemberAccountDeposit;
	}

    public void updateGatheringMemberAccountBalance(long gatheringMemberAccountBalance) {
        this.gatheringMemberAccountBalance = gatheringMemberAccountBalance;
    }

    public void increaseBalance(Long amount) {
        this.gatheringMemberAccountBalance += amount;
    }
}