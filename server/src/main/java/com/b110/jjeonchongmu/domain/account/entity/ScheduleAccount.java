package com.b110.jjeonchongmu.domain.account.entity;

import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
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
@DiscriminatorValue("SCHEDULE")
public class ScheduleAccount extends Account {

    @OneToOne(mappedBy = "scheduleAccount" , fetch = FetchType.LAZY)
    private Schedule schedule;
}