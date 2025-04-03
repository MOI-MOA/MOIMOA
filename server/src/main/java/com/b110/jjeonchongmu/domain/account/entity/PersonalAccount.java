package com.b110.jjeonchongmu.domain.account.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("PERSONAL")
public class PersonalAccount extends Account {

    @Column(name = "account_no", nullable = true)
    private String accountNo;

    @OneToMany(mappedBy = "personalAccount" , fetch = FetchType.LAZY)
    private List<AutoPayment> autoPayments = new ArrayList<>();

    public PersonalAccount(User user, String accountNo, String accountPw) {
        super(user, accountPw,0L);
        this.autoPayments = null;
        this.accountNo = accountNo;
    }

}
