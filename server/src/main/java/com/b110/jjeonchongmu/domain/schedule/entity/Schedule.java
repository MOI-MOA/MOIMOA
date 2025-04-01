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
@Builder
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

    @Column(name = "schedule_place", nullable = false)
    private String place;

    @Column(name = "schedule_start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "per_budget", nullable = false)
    private Long perBudget;

    @Column(name = "penalty_apply_date", nullable = false)
    private LocalDateTime penaltyApplyDate;

    @Column(name = "penalty_rate")
    private int penaltyRate;

    @Column(name = "schedule_status")
    private int status;

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleMember> attendees = new ArrayList<>();
    // 제목 수정
    public void updateTitle(String title) {
        this.title = title;
    }
    // 상세 내용 수정
    public void updateDetail(String detail) {
        this.detail = detail;
    }
    // 장소 수정
    public void updatePlace(String place) {
        this.place = place;
    }
    // 1인당 예산 수정
    public void updatePerBudget(Long perBudget) {
        this.perBudget = perBudget;
    }
    // 일정 계좌 수정
    public void updateScheduleAccount(ScheduleAccount scheduleAccount) { this.scheduleAccount = scheduleAccount; }

    // 모든 필드 한번에 수정
    public void updateSchedule(String title, String detail, String place, 
                             LocalDateTime startTime, Long perBudget, 
                             LocalDateTime penaltyApplyDate) {
        this.title = title;
        this.detail = detail;
        this.place = place;
        this.startTime = startTime;
        this.perBudget = perBudget;
        this.penaltyApplyDate = penaltyApplyDate;
    }
}