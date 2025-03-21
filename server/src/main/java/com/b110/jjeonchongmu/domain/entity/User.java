package com.b110.jjeonchongmu.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "user_key", nullable = false)
    private String userKey;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth", nullable = false)
    private String birth;

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

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<GatheringMember> gatheringMembers;

    @OneToOne(mappedBy = "user" , fetch = FetchType.LAZY)
    private Schedule schedule;

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<ScheduleMember> scheduleMembers;

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<Notification> notifications;

}