package com.b110.jjeonchongmu.domain.account.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trade")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User accountHolder;

    @Column(name = "account_no", nullable = false)
    private String accountNo;

    @Column(name = "account_balance", nullable = false)
    private Integer AccountBalance;

    @Column(name = "account_pw", nullable = false)
    private String AccountPw;


}
