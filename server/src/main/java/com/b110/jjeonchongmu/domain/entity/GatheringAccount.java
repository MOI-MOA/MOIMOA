package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gahtering_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatheringAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gahtering_account_id", nullable = false)
    private Long gahteringAccountId;

    @ManyToOne
    @JoinColumn(name = "gahtering_account_name_id", nullable = false)
    private Gathering Gathering;

    @Column(name = "gahtering_account_no", nullable = true)
    private Long gahteringAccountNo;

    @Column(name = "gahtering_account_balance", nullable = false)
    private Integer gahteringAccountBalance = 0;

    @Column(name = "gahtering_account_pw", nullable = false)
    private Integer gahteringAccountPw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}