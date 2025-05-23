package com.b110.jjeonchongmu.domain.user.entity;

import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.notification.entity.Notification;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 사용자 엔티티 Spring Security의 UserDetails를 구현하여 인증/인가에 사용
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String userKey;

	@Column(nullable = false)
	private String password;

	@Column
	private LocalDate birth;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	// User 클래스 내에서
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private PersonalAccount personalAccount;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<GatheringAccount> gatheringAccounts = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<ScheduleAccount> scheduleAccounts = new ArrayList<>();

	@OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
	private List<Gathering> gatherings = new ArrayList<>();

	@OneToMany(mappedBy = "gatheringMemberUser", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GatheringMember> gatheringMembers = new ArrayList<>();

	@OneToMany(mappedBy = "subManager", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Schedule> schedules = new ArrayList<>();

	@OneToMany(mappedBy = "scheduleMember", fetch = FetchType.LAZY)
	private List<ScheduleMember> scheduleMembers;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notification> notifications = new ArrayList<>();

	@Builder
	public User(String name, String email, String userKey, String password, LocalDate birth) {
		this.name = name;
		this.email = email;
		this.userKey = userKey;
		this.password = password;
		this.birth = birth;
		this.createdAt = LocalDateTime.now();
	}

	/**
	 * 비밀번호 변경
	 */
	public void changePassword(String newPassword) {
		this.password = newPassword;
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
	public String getPassword() {
		return this.password;
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
//        gatheringMember.setUser(this);
	}

	/**
	 * 일정 추가
	 */
	public void addSchedule(Schedule schedule) {
		this.schedules.add(schedule);
//        schedule.setUser(this);
	}

	/**
	 * 알림 추가
	 */
	public void addNotification(Notification notification) {
		this.notifications.add(notification);
//        notification.setUser(this);
	}

	public String getName() {
		return this.name;
	}

	public void updateName(String name) {
		this.name = name;
	}
}