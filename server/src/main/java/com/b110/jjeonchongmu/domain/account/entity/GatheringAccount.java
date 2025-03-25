package com.b110.jjeonchongmu.domain.account.entity;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "gathering_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatheringAccount extends Account {

    @OneToOne(mappedBy = "gatheringAccount" , fetch = FetchType.LAZY)
    private Gathering gathering;
}