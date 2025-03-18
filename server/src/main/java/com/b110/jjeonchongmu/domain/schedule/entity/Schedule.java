package com.b110.jjeonchongmu.domain.schedule.entity;

import com.b110.jjeonchongmu.domain.group.entity.Group;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "sub_manager_id", nullable = false)
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
}