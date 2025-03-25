package com.b110.jjeonchongmu.domain.account.entity;
import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "personal_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalAccount extends Account {

    @OneToMany(mappedBy = "personalAccount" , fetch = FetchType.LAZY)
    private List<AutoPayment> autoPayments;
}