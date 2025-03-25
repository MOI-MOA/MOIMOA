package com.b110.jjeonchongmu.domain.account.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("PERSONAL")  // 이 부분을 추가
public class PersonalAccount extends Account {

    @OneToMany(mappedBy = "personalAccount" , fetch = FetchType.LAZY)
    private List<AutoPayment> autoPayments;
}