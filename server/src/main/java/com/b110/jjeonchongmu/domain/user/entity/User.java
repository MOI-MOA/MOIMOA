package com.b110.jjeonchongmu.domain.user.entity;

import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.notification.entity.Notification;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 사용자 엔티티
 * Spring Security의 UserDetails를 구현하여 인증/인가에 사용
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long personalAccountPW;  // 개인 계좌 비밀번호

    @Column(nullable = false)
    private Integer pin;  // PIN 번호

    @Column(nullable = false)
    private Boolean scheduleNotificationEnabled;  // 일정 알림 설정

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // cascade = CascadeType.REMOVE : user 삭제시 해당 personalAccount 자동삭제
    @OneToOne(mappedBy = "user" , fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private PersonalAccount personalAccount;

    // cascade = CascadeType.REMOVE : user 삭제시 해당 gatheringAccount 자동삭제
    @OneToOne(mappedBy = "user" , fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private GatheringAccount gatheringAccount;

    // cascade = CascadeType.REMOVE : user 삭제시 해당 scheduleAccount 자동삭제
    @OneToOne(mappedBy = "user" , fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private ScheduleAccount scheduleAccount;

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<Gathering> gatherings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GatheringMember> gatheringMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<ScheduleMember> scheduleMembers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public User(String email, String name, String password, Long personalAccountPW, 
                 Integer pin, Boolean scheduleNotificationEnabled) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.personalAccountPW = personalAccountPW;
        this.pin = pin;
        this.scheduleNotificationEnabled = scheduleNotificationEnabled;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 일정 알림 설정 변경
     */
    public void toggleScheduleNotification() {
        this.scheduleNotificationEnabled = !this.scheduleNotificationEnabled;
    }

    // UserDetails 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return String.valueOf(this.userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 모임 멤버 추가
     */
    public void addGatheringMember(GatheringMember gatheringMember) {
        this.gatheringMembers.add(gatheringMember);
        gatheringMember.setUser(this);
    }

    /**
     * 일정 추가
     */
    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setUser(this);
    }

    /**
     * 알림 추가
     */
    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setUser(this);
    }
}