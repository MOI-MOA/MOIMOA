package com.b110.jjeonchongmu.domain.account.entity;

import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("GATHERING")  // 이 부분을 추가
public class GatheringAccount extends Account {

    @OneToOne(mappedBy = "gatheringAccount" , fetch = FetchType.LAZY)
    private Gathering gathering;

    public GatheringAccount(User user, String accountNo, String accountPw, Gathering gathering) {
        super(user, accountNo, accountPw);
        this.gathering = gathering;
    }
}
