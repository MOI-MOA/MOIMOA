package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gathering_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatheringMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_member_id", nullable = false)
    private Long gatheringMemberId;

    @ManyToOne
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering Gathering;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private PersonalAccount.User user;

    @Column(name = "gathering_attend_count")
    private Integer gatheringAttendCount;

    @Column(name = "gathering_member_account_balance")
    private Integer gatheringMemberAccountBalance;

    @Column(name = "gathering_member_account_deposit")
    private Integer gatheringMemberAccountDeposit;

    @Column(name = "gathering_payment_status")
    private Boolean gatheringPaymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}