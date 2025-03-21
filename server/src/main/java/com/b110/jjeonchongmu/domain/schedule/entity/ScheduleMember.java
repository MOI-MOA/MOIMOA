package com.b110.jjeonchongmu.domain.schedule.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_member_id", nullable = false)
    private Long scheduleMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User scheduleMember;

    @Column(name = "schedule_is_check", nullable = true)
    private Boolean scheduleIsCheck;
}
