package com.b110.jjeonchongmu.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "gathering")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Gathering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gahtering_id", nullable = false)
    private Long gahteringId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User gatheringManager;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_account_id" , nullable = false)
    private GatheringAccount gatheringAccount;

    @Column(name = "gahtering_name", nullable = false, length = 255)
    private String gahteringName;

    @Column(name = "gahtering_introduction")
    private String gahteringIntroduction;

    @Column(name = "deposit_date")
    private String depositDate;

    @Column(name = "basic_fee")
    private Integer basicFee;

    @Column(name = "penalty_rate")
    private Integer penaltyRate;

    @Column(name = "member_count")
    private Integer memberCount;

    @Column(name = "gahtering_deposit", nullable = false)
    private Integer gahteringDeposit;

    @OneToMany(mappedBy = "gathering" , fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "gathering" , fetch = FetchType.LAZY)
    private List<GatheringMember> gatheringMembers;

}