package com.b110.jjeonchongmu.domain.account.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_no", nullable = false)
    private String accountNo;

    @Column(name = "account_balance", nullable = false)
    private Integer AccountBalance;

    @Column(name = "account_pw", nullable = false)
    private String AccountPw;

    public void decreaseBalance(Integer amount) {
        this.AccountBalance -= amount;
    }

    public void increaseBalance(Integer amount) {
        this.AccountBalance += amount;
    }
}
