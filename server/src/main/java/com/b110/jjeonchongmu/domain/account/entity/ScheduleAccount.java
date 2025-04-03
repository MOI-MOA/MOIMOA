package com.b110.jjeonchongmu.domain.account.entity;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("SCHEDULE")
@Builder
public class ScheduleAccount extends Account {

    @OneToOne(mappedBy = "scheduleAccount" , fetch = FetchType.LAZY)
    private Schedule schedule;

    public ScheduleAccount(User user, String accountPw,Schedule schedule,Long accountBalance) {
        super(user, accountPw, accountBalance);
        this.schedule = schedule;
        updateDtype(AccountType.SCHEDULE);
    }

}
