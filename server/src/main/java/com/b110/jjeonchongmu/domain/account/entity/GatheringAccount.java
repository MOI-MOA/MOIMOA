package com.b110.jjeonchongmu.domain.account.entity;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "gathering_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatheringAccount extends Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_account_id", nullable = false)
    private Long gatheringAccountId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User gatheringAccountHolder;
//
//    @Column(name = "gathering_account_no", nullable = true)
//    private Long gatheringAccountNo;
//
//    @Column(name = "gathering_account_balance", nullable = false)
//    private Integer gatheringAccountBalance = 0;
//
//    @Column(name = "gathering_account_pw", nullable = false)
//    private Integer gatheringAccountPw;

    @OneToOne(mappedBy = "gatheringAccount" , fetch = FetchType.LAZY)
    private Gathering gathering;

    @OneToMany(mappedBy = "gatheringAccount" , fetch = FetchType.LAZY)
    private List<AutoPayment> autoPayments;
}