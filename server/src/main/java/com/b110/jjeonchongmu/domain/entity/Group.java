package com.b110.jjeonchongmu.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private PersonalAccount.User manager;

    @Column(name = "group_name", nullable = false, length = 255)
    private String groupName;

    @Column(name = "group_introduction")
    private String groupIntroduction;

    @Column(name = "deposit_date")
    private String depositDate;

    @Column(name = "basic_fee")
    private Integer basicFee;

    @Column(name = "penalty_rate")
    private Integer penaltyRate;

    @Column(name = "member_count")
    private Integer memberCount;

    @Column(name = "group_deposit", nullable = false)
    private Integer groupDeposit;
}