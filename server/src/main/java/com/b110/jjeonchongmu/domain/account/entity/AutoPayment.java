package com.b110.jjeonchongmu.domain.account.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auto_payment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AutoPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_payment_id", nullable = false)
    private Long autoPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_account_id", nullable = false)
    private PersonalAccount personalAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gahtering_account_id", nullable = false)
    private GatheringAccount gatheringAccount;

    @Column(name = "auto_payment_amount", nullable = true)
    private Integer autoPaymentAmount;

    // 매월 며칠인지 표시
    // ex) 14 면 매월 14일에 자동 입금
    @Column(name = "auto_payment_date", nullable = true)
    private Integer autoPaymentDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    public void updateAutoPaymentAmount(Integer autoPaymentAmount) {
        this.autoPaymentAmount = autoPaymentAmount;
    }
    public void updateAutoPaymentDate(Integer autoPaymentDate) {
        this.autoPaymentDate = autoPaymentDate;
    }
    public void updateIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}