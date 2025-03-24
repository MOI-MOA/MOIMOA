package com.b110.jjeonchongmu.domain.schedule.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_member")
@Getter
@NoArgsConstructor
public class ScheduleMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_member_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User scheduleMember;

    @Column(name = "schedule_is_check")
    private Boolean scheduleIsCheck;

    @Builder
    public ScheduleMember(Schedule schedule, User user, Boolean scheduleIsCheck) {
        this.schedule = schedule;
        this.scheduleMember = user;
        this.scheduleIsCheck = scheduleIsCheck;
    }

    public void checkAttendance() {
        this.scheduleIsCheck = true;
    }

    public User getUser() {
        return this.scheduleMember;
    }
}
