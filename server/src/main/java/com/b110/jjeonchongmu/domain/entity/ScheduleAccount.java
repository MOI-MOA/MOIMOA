package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAccount extends Account{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_account_id", nullable = false)
    private Long scheduleAccountId;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User scheduleAccountHolder;
//
//    @Column(name = "schedule_account_no", nullable = true)
//    private Long scheduleAccountNo;
//
//    @Column(name = "schedule_account_balance", nullable = true)
//    private Integer scheduleAccountBalance;
//
//    @Column(name = "schedule_account_pw", nullable = false)
//    private Integer scheduleAccountPw;

    @OneToOne(mappedBy = "scheduleAccount" , fetch = FetchType.LAZY)
    private Schedule schedule;

}