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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_balance", nullable = false)
    private Long accountBalance;

    @Column(name = "account_pw", nullable = false)
    private String accountPw;

    @Column(name = "dtype", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AccountType dtype;

    public Account(User user, String accountPw, Long accountBalance) {
        this.user = user;
        this.accountPw = accountPw;
        this.accountBalance = accountBalance;
    }

    public void decreaseBalance(Long amount) {
        this.accountBalance -= amount;
    }

    public void increaseBalance(Long amount) {
        this.accountBalance += amount;
    }

    public void updateDtype(AccountType accountType){this.dtype = accountType;}
}

