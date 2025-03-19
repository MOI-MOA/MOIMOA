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

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<GatheringAccount> GatheringAccount;

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<Gathering> Gathering;

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<Gathering> GatheringMember;

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



}