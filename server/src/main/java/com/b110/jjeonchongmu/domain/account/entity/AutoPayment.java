package com.b110.jjeonchongmu.domain.account.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auto_payment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_payment_id", nullable = false)
    private Long autoPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_account_id", nullable = false)
    private PersonalAccount personalAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_account_id", nullable = false)
    private GatheringAccount gatheringAccount;

    @Column(name = "auto_payment_amount")
    private Long autoPaymentAmount;

    @Column(name = "auto_payment_date", nullable = true)
    private Integer autoPaymentDate;  // 1-28일 사이 값

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    public void updateAutoPaymentAmount(Long amount) {
        this.autoPaymentAmount = amount;
    }

    public void updateAutoPaymentDate(Integer date) {
        if (date < 1 || date > 28) {
            throw new IllegalArgumentException("자동이체 날짜는 1-28일 사이여야 합니다.");
        }
        this.autoPaymentDate = date;
    }

    public void updateIsActive(Boolean active) {
        this.isActive = active;
    }
}