package com.b110.jjeonchongmu.domain.schedule.entity;

import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleUpdateDTO;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User subManager;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule_account_id")
    private ScheduleAccount scheduleAccount;

    @Column(name = "schedule_title", nullable = false)
    private String title;

    @Column(name = "schedule_detail", nullable = false)
    private String detail;

    @Column(name = "schedule_place")
    private String place;

    @Column(name = "schedule_start_time")
    private LocalDateTime startTime;

    @Column(name = "per_budget")
    private Long perBudget;

    @Column(name = "penalty_apply_date")
    private LocalDateTime penaltyApplyDate;

    @Column(name = "penalty_rate")
    private int penalty_rate;

    @Column(name = "schedule_status")
    private int status;

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleMember> attendees = new ArrayList<>();
}