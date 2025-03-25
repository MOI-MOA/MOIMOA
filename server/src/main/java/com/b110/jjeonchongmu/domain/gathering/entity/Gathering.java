package com.b110.jjeonchongmu.domain.gathering.entity;

import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "gathering")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gathering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_id", nullable = false)
    private Long gatheringId;

    //총무아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User manager;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_account_id" , nullable = false)
    private GatheringAccount gatheringAccount;

    @Column(name = "gathering_name", nullable = false, length = 255)
    private String gatheringName;

    @Column(name = "gathering_introduction")
    private String gatheringIntroduction;

    @Column(name = "deposit_date")
    private String depositDate;

    @Column(name = "basic_fee")
    private Long basicFee;

    @Column(name = "penalty_rate")
    private Integer penaltyRate;

    @Column(name = "member_count")
    private Integer memberCount;

    @Column(name = "gathering_deposit", nullable = false)
    private Long gatheringDeposit;

    @OneToMany(mappedBy = "gathering" , fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "gathering" , fetch = FetchType.LAZY)
    private List<GatheringMember> gatheringMembers;

    @Builder
    public Gathering(User manager, GatheringAccount gatheringAccount, String gatheringName,
                    String gatheringIntroduction, int memberCount, int penaltyRate, String depositDate,
                    Long basicFee, Long gatheringDeposit) {
        this.manager = manager;
        this.gatheringAccount = gatheringAccount;
        this.gatheringName = gatheringName;
        this.gatheringIntroduction = gatheringIntroduction;
        this.memberCount = memberCount;
        this.penaltyRate = penaltyRate;
        this.depositDate = depositDate;
        this.basicFee = basicFee;
        this.gatheringDeposit = gatheringDeposit;
    }

    public Long getManagerId() {
        return manager != null ? manager.getUserId() : null;
    }

    public void updateGathering(String gatheringName, String gatheringIntroduction, int memberCount,
                              int penaltyRate, String depositDate, Long basicFee, Long gatheringDeposit) {
        this.gatheringName = gatheringName;
        this.gatheringIntroduction = gatheringIntroduction;
        this.memberCount = memberCount;
        this.penaltyRate = penaltyRate;
        this.depositDate = depositDate;
        this.basicFee = basicFee;
        this.gatheringDeposit = gatheringDeposit;
    }

    public void updateMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}