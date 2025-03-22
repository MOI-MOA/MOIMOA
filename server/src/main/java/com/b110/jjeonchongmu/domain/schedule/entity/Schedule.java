package com.b110.jjeonchongmu.domain.schedule.entity;

import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.schedule.dto.ScheduleUpdateDto;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_account_id", nullable = false)
    private ScheduleAccount scheduleAccount;

    @Column(name = "schedule_title", nullable = false)
    private String title;

    @Column(name = "schedule_detail", nullable = false)
    private String detail;

    @Column(name = "schedule_place", nullable = true)
    private String place;

    @Column(name = "schedule_start_time", nullable = true)
    private LocalDateTime startTime;

    @Column(name = "per_budget", nullable = true)
    private Long perBudget;

    @Column(name = "total_budgget", nullable = true)
    private Long totalBudget;

    @Column(name = "penalty_apply_date", nullable = true)
    private Date penaltyApplyDate;

    @Column(name = "schedule_status", nullable = true)
    private int status;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ScheduleMember> attendees;

    @Builder
    public Schedule(Gathering gathering, User manager, String title, String detail,
                   String place, LocalDateTime startTime, long perBudget,
                   long totalBudget, LocalDateTime penaltyApplyDate) {
        this.gathering = gathering;
        this.manager = manager;
        this.title = title;
        this.detail = detail;
        this.place = place;
        this.startTime = startTime;
        this.perBudget = perBudget;
        this.totalBudget = totalBudget;
//        this.penaltyApplyDate = penaltyApplyDate;
        this.status = 0;
    }

    public void update(ScheduleUpdateDto dto) {
        this.title = dto.getScheduleTitle();
        this.detail = dto.getScheduleDetail();
        this.place = dto.getSchedulePlace();
        this.startTime = dto.getScheduleStartDate();
//        this.totalBudget = dto.getTotalBudget();
//        this.penaltyApplyDate = dto.getPenaltyApplyDate();
    }
}