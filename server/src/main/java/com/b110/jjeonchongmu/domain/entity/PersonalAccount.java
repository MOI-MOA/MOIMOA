package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "personal_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_account_id", nullable = false)
    private Long personalAccountId;

    @Column(name = "personal_account_no", nullable = false, length = 255)
    private String personalAccountNo;

    @Column(name = "personal_account_balance", nullable = false)
    private Integer personalAccountBalance;

    @Column(name = "personal_account_pw", nullable = false)
    private String personalAccountPw;

    @Entity
    @Table(name = "user")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    }
}