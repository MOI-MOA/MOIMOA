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

    @Column(name = "is_penalty_apply")
    private boolean isPenaltyApply;

    @Column(name = "is_attend")
    private Boolean isAttend;

    // 페이백 대상으로 지정
    public void updateIsPenaltyApplyToTrue(){
        this.isPenaltyApply = true;
    }

    // 일정에 참여
    public void updateIsAttenedToTrue() { this.isAttend = true;}

    // 일정에 미참여로 변경
    public void updateIsAttenedToFalse() { this.isAttend = false;}

    // 일정 확인한걸로 바꾸는 로직
    public void updateScheduleIsCheckToTrue(){this.scheduleIsCheck = true; }
}
