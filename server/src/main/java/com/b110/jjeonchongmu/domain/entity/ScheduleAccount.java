package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_account_id", nullable = false)
    private Long scheduleAccountId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "schedule_account_name_id", nullable = false)
    private Long scheduleAccountNameId;

    @Column(name = "schedule_account_no", nullable = true)
    private Long scheduleAccountNo;

    @Column(name = "schedule_account_balance", nullable = true)
    private Integer scheduleAccountBalance;

    @Column(name = "schedule_account_pw", nullable = false)
    private Integer scheduleAccountPw;
}