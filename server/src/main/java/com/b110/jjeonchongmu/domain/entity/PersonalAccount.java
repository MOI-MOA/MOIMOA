package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User personalAccountHolder;

    @Column(name = "personal_account_no", nullable = false, length = 255)
    private String personalAccountNo;

    @Column(name = "personal_account_balance", nullable = false)
    private Integer personalAccountBalance;

    @Column(name = "personal_account_pw", nullable = false)
    private String personalAccountPw;

    @OneToMany(mappedBy = "personalAccount" , fetch = FetchType.LAZY)
    private List<AutoPayment> autoPayments;


}