package com.b110.jjeonchongmu.domain.account.entity;

import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("GATHERING")  // 이 부분을 추가
public class GatheringAccount extends Account {

    @Column(name = "account_no", nullable = true)
    private String accountNo;

    @OneToOne(mappedBy = "gatheringAccount" , fetch = FetchType.LAZY)
    private Gathering gathering;


    public GatheringAccount(User user, String accountNo, String accountPw, Gathering gathering) {
        super(user, accountPw,0L);
        this.gathering = gathering;
        this.accountNo = accountNo;
    }

    public GatheringAccount(User user, String accountNo, String accountPw){
        super(user,  accountPw,0L);
        this.accountNo = accountNo;

    }


}
