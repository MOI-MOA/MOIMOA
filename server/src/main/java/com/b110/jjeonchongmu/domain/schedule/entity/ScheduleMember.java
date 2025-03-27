package com.b110.jjeonchongmu.domain.schedule.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
