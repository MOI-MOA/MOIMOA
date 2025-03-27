package com.b110.jjeonchongmu.domain.account.entity;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
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
    private Long accountBalance;

    @Column(name = "account_pw", nullable = false)
    private String accountPw;

    @Column(name = "dtype", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AccountType dtype;

    public Account(User user, String accountNo, String accountPw) {
        this.user = user;
        this.accountNo = accountNo;
        this.accountPw = accountPw;
        this.accountBalance = 0L;
    }

    public void decreaseBalance(Long amount) {
        this.accountBalance -= amount;
    }

    public void increaseBalance(Long amount) {
        this.accountBalance += amount;
    }
}

