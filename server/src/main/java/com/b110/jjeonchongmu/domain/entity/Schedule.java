package com.b110.jjeonchongmu.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gahtering_id", nullable = false)
    private Gathering Gathering;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User subManager;

    @Column(name = "schedule_title", nullable = false)
    private String scheduleTitle;

    @Column(name = "schedule_detail", nullable = false)
    private String scheduleDetail;

    @Column(name = "schedule_place", nullable = true)
    private String schedulePlace;

    @Column(name = "schedule_start_time", nullable = true)
    private LocalDateTime scheduleStartTime;

    @Column(name = "per_budget", nullable = true)
    private Long perBudget;

    @Column(name = "total_budget", nullable = true)
    private Long totalBudget;

    @Column(name = "penalty_apply_date", nullable = true)
    private LocalDateTime penaltyApplyDate;

    @Column(name = "schedule_status", nullable = true)
    private Integer scheduleStatus;

    // cascade = CascadeType.REMOVE : schedule이 삭제되면 scheduleAccount 자동 삭제
    @OneToOne(mappedBy = "schedule" , fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private ScheduleAccount scheduleAccount;

    @OneToMany(mappedBy = "schedule" , fetch = FetchType.LAZY)
    private List<ScheduleMember> scheduleMembers;
}