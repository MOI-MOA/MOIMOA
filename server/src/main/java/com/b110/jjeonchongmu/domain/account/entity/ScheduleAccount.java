package com.b110.jjeonchongmu.domain.account.entity;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAccount extends Account {

    @OneToOne(mappedBy = "scheduleAccount" , fetch = FetchType.LAZY)
    private Schedule schedule;
}